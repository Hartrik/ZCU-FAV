
/**
 * Implementuje základní funkce pro práci s vfs.
 *
 * @author: Patrik Harag
 * @version: 2017-12-23
 */

#include <stdlib.h>
#include <assert.h>
#include "vfs.h"
#include "io.h"
#include "vfs-module-allocation.h"
#include "vfs-module-utils.h"


VFS* vfs_create(char* file_name) {
    VFS* vfs = (VFS*) calloc(1, sizeof(VFS));

    vfs->file_name = malloc((strlen(file_name) + 1) * sizeof(char));
    strcpy(vfs->file_name, file_name);

    vfs->initialized = false;
    vfs->file = NULL;
    vfs->boot_record = (BootRecord*) calloc(1, sizeof(BootRecord));
    vfs->mft = (Mft*) calloc(1, sizeof(Mft));
    vfs->bitmap = (Bitmap*) calloc(1, sizeof(Bitmap));

    return vfs;
}

void vfs_free(VFS* vfs) {
    free(vfs->file_name);

    vfs_boot_record_free(vfs->boot_record);
    free(vfs->boot_record);

    vfs_mft_free(vfs->mft);
    free(vfs->mft);

    vfs_bitmap_free(vfs->bitmap);
    free(vfs->bitmap);
}

MftItem* vfs_init(VFS* vfs, int32_t disk_size, int32_t cluster_size, int32_t mft_size) {
    if (vfs->initialized) {
        vfs_boot_record_free(vfs->boot_record);
        vfs_mft_free(vfs->mft);
        vfs_bitmap_free(vfs->bitmap);

        vfs->initialized = false;
    }

    vfs_boot_record_init(vfs->boot_record, disk_size, cluster_size, mft_size);
    vfs_mft_init(vfs->mft, mft_size);
    vfs_bitmap_init(vfs->bitmap, vfs->boot_record->cluster_count);
    vfs->initialized = true;

    // MFT má vždy alespoň jednu položku
    return vfs_create_root_dir(vfs);
}

MftItem* vfs_create_root_dir(VFS* vfs) {
    assert(vfs->initialized);

    MftItem* dir = vfs_mkdir(vfs, "/", NULL);
    assert(dir != NULL);
    return dir;
}

MftItem* vfs_mkdir(VFS* vfs, char* name, MftItem* dir) {
    int32_t uid = vfs_mft_generate_random_uid(vfs->mft);

    // alokace mft položky
    int mft_item_count = 1;
    MftItem* free_mft_items[mft_item_count];
    if (!vfs_mft_find_free_mft_items(vfs->mft, mft_item_count, (MftItem **) &free_mft_items)) {
        printf("NOT ENOUGH MFT ITEMS\n");
        return NULL;
    }

    if (dir != NULL) {
        // přidání souboru do složky - může také selhat na nedostatek clusterů
        Result add_into_dir_result = vfs_add_item_into_dir(vfs, dir, uid);
        if (add_into_dir_result != RESULT_OK)
            return NULL;
    }

    // nastavení mft položky
    MftItem* item = free_mft_items[0];
    item->uid = uid;
    item->parent_uid = (dir != NULL) ? dir->uid : VFS_MFT_UID_ITEM_FREE;
    size_t strncpy_len = utils_min(VFS_MFT_ITEM_NAME_LENGTH, strlen(name));
    strncpy(item->item_name, name, strncpy_len);
    item->is_directory = true;
    item->item_size = 0;
    item->item_order = 0;
    item->item_total = 1;

    return item;
}

Result vfs_create_file(VFS* vfs, char* name, FILE* src_file, MftItem* dir) {
    assert(dir != NULL);

    int32_t uid = vfs_mft_generate_random_uid(vfs->mft);

    fseek(src_file, 0, SEEK_END);  // na konec souboru
    long size_bytes = ftell(src_file);
    fseek(src_file, 0, SEEK_SET);  // zpět na začátek

    MftFragment *fragments;
    int32_t fragment_count;
    Result initialization = vfs_init_new_file(vfs, uid, name, size_bytes, dir, &fragments, &fragment_count);
    if (initialization != RESULT_OK) {
        return initialization;
    }

    // zápis do souboru
    io_update_mft(vfs);
    io_update_bitmap(vfs);
    io_write_data_from_file(vfs, fragments, fragment_count, src_file, (int32_t) size_bytes);

    free(fragments);
    return RESULT_OK;
}

Result vfs_add_item_into_dir(VFS *vfs, MftItem *dir, int32_t uid) {
    assert(dir != NULL || dir->is_directory);
    assert(dir->item_size % sizeof(int32_t) == 0);

    // načtení seznamu souborů
    int size_bytes = dir->item_size + sizeof(int32_t);  // + ten co přidáme...
    char data[size_bytes];

    if (!io_load_data_into_buffer(vfs, dir, data)) {
        return RESULT_ERROR;
    }

    // pridání nového souboru na konec seznamu
    ((int32_t*) data)[dir->item_size / sizeof(int32_t)] = uid;

    return vfs_change_content(vfs, dir, data, size_bytes);
}

Result vfs_remove_item_from_dir(VFS *vfs, MftItem *dir, int32_t uid) {
    assert(dir != NULL || dir->is_directory);
    assert(dir->item_size % sizeof(int32_t) == 0);

    // načtení seznamu souborů
    char data[dir->item_size + 1];
    if (!io_load_data_into_buffer(vfs, dir, data)) {
        return RESULT_ERROR;
    }

    // odstranění položky
    int new_data_size = dir->item_size - sizeof(int32_t);
    char new_data[new_data_size];
    int entries = dir->item_size / sizeof(int32_t);

    int i = 0, j = 0;
    for (; i < entries; ++i) {
        int32_t next_uid = ((int32_t*) data)[i];

        if (next_uid != uid) {
            ((int32_t*) new_data)[j++] = next_uid;
        }
    }

    if (i == j) {
        printf("Item %d not found\n", uid);
        return RESULT_OK;
    } else if (i - j > 1) {
        printf("Item %d was present multiple times\n", uid);
    }

    return vfs_change_content(vfs, dir, new_data, j * sizeof(int32_t));
}

Result vfs_change_content(VFS* vfs, MftItem* first_item, char* data, int32_t size_bytes) {
    vfs_clean_content(vfs, first_item, -1, true);

    MftFragment *fragments;
    int32_t fragment_count;
    Result initialization = vfs_init_reallocation(vfs, first_item, size_bytes, 0, &fragments, &fragment_count);
    if (initialization != RESULT_OK) {
        return initialization;
    }

    // zápis do souboru
    io_update_mft(vfs);
    io_update_bitmap(vfs);
    io_write_data_from_buffer(vfs, fragments, fragment_count, data, size_bytes);

    free(fragments);
    return RESULT_OK;
}

Result vfs_move(VFS* vfs, MftItem* item, char* new_name, MftItem* old_dir, MftItem* new_dir) {
    if (new_dir != old_dir) {
        // aktualizace odkazů rodičovských adresářů

        Result r = vfs_add_item_into_dir(vfs, new_dir, item->uid);

        if (r != RESULT_OK) {
            return RESULT_WARNING;
        }

        if (vfs_remove_item_from_dir(vfs, old_dir, item->uid) != RESULT_OK) {
            // raději to vypnem
            // jinak bychom mohli zanechat nějaké vedlejší efekty...
            return RESULT_ERROR;
        }
    }

    item->parent_uid = new_dir->uid;

    size_t strncpy_len = utils_min(VFS_MFT_ITEM_NAME_LENGTH, strlen(new_name));
    strncpy(item->item_name, new_name, strncpy_len);

    return RESULT_OK;
}

Result vfs_copy(VFS* vfs, MftItem* src_item, int32_t src_item_index,
                char* copy_name, MftItem* copy_dir) {

    int32_t uid = vfs_mft_generate_random_uid(vfs->mft);
    int32_t size_bytes = src_item->item_size;

    MftFragment *fragments;
    int32_t fragment_count;
    Result initialization = vfs_init_new_file(
            vfs, uid, copy_name, size_bytes, copy_dir, &fragments, &fragment_count);

    if (initialization != RESULT_OK) {
        return initialization;
    }

    // zápis do souboru
    io_update_mft(vfs);
    io_update_bitmap(vfs);

    // kopírování dat
    int32_t cluster_count = vfs_count_clusters(vfs, src_item);

    int32_t dest_clusters[cluster_count];
    vfs_collect_clusters_from_fragments(fragments, fragment_count, dest_clusters);

    int32_t src_clusters[cluster_count];
    vfs_collect_clusters_from_mft_items(vfs, src_item, src_item_index, src_clusters);

    for (int i = 0; i < cluster_count; ++i) {
        io_copy_cluster(vfs, src_clusters[i], dest_clusters[i]);
    }

    free(fragments);
    return RESULT_OK;
}

Result vfs_init_new_file(VFS* vfs, int32_t uid, char* name, long size_bytes, MftItem* dir,
                         MftFragment** out_fragments, int32_t* out_fragment_count) {

    // nalezení volných clusterů
    MftFragment *fragments;
    int32_t fragment_count = 0;
    if (!vfs_allocate_clusters(vfs, size_bytes, 0, &fragments, &fragment_count)) {
        return RESULT_WARNING;
    }

    // nalezení volných položek v MFT
    //  podle počtu fragmentů se určí počet mft items
    int mft_item_count = utils_div_ceil(fragment_count, VFS_MFT_FRAGMENTS_COUNT);
    mft_item_count = utils_max(1, mft_item_count);  // i pokud soubor nemá obsah...
    MftItem *free_mft_items[mft_item_count];
    if (!vfs_allocate_mft_items(vfs, mft_item_count, (MftItem **) &free_mft_items, uid)) {
        printf("NOT ENOUGH MFT ITEMS\n");

        // změtné uvolnění
        vfs_release_clusters(vfs, fragments, fragment_count);

        return RESULT_WARNING;
    }

    // přidání souboru do složky
    Result add_into_dir_result = vfs_add_item_into_dir(vfs, dir, uid);
    if (add_into_dir_result != RESULT_OK) {
        // může také selhat na nedostatek clusterů nebo mft items atd.

        // změtné uvolnění
        vfs_release_clusters(vfs, fragments, fragment_count);
        vfs_release_mft_items(vfs, free_mft_items, mft_item_count);

        return add_into_dir_result;
    }

    // soubor je možné vytvořit

    // update MFT
    for (int i = 0; i < mft_item_count; ++i) {
        MftItem* item = free_mft_items[i];
        item->uid = uid;
        item->item_order = i;
        item->item_total = mft_item_count;
        item->is_directory = false;

        if (i == 0) {
            item->parent_uid = dir->uid;
            size_t strncpy_len = utils_min(VFS_MFT_ITEM_NAME_LENGTH, strlen(name));
            strncpy(item->item_name, name, strncpy_len);
            item->item_size = (int32_t) size_bytes;
        }

        // nalinkování fragmentů
        int32_t done = (i * VFS_MFT_FRAGMENTS_COUNT);
        int32_t remaining = fragment_count - done;
        int32_t to_copy = utils_min(VFS_MFT_FRAGMENTS_COUNT, remaining);
        for (int j = 0; j < to_copy; ++j) {
            item->fragments[j].count = fragments[done + j].count;
            item->fragments[j].start_index = fragments[done + j].start_index;
        }
    }

    *out_fragments = fragments;
    *out_fragment_count = fragment_count;
    return RESULT_OK;
}

Result vfs_init_reallocation(VFS* vfs, MftItem* first_item, int32_t size_bytes,
                         int32_t preferred_min_cluster_index,
                         MftFragment** out_fragments, int32_t* out_fragment_count) {

    // nalezení volných clusterů
    MftFragment* fragments;
    int32_t fragment_count = 0;
    if (!vfs_allocate_clusters(vfs, size_bytes, preferred_min_cluster_index, &fragments, &fragment_count)) {
        return RESULT_WARNING;
    }

    // nalezení volných položek v MFT
    //  podle počtu fragmentů se určí počet mft items
    int mft_item_count = utils_div_ceil(fragment_count, VFS_MFT_FRAGMENTS_COUNT);
    int free_mft_item_count = utils_max(0, mft_item_count - 1);
    MftItem* free_mft_items[free_mft_item_count];
    if (!vfs_mft_find_free_mft_items(vfs->mft, free_mft_item_count, (MftItem **) &free_mft_items)) {
        printf("NOT ENOUGH MFT ITEMS\n");

        // zpětné uvolnění
        vfs_release_clusters(vfs, fragments, fragment_count);

        return RESULT_WARNING;
    }

    // kopírování je možné provést

    // update MFT
    for (int i = 0; i < mft_item_count; ++i) {
        MftItem* item = NULL;

        if (i == 0) {
            // první položka
            item = first_item;
            item->item_size = size_bytes;
        } else {
            item = free_mft_items[i-1];
            assert(item != first_item);

            item->uid = first_item->uid;
            item->is_directory = first_item->is_directory;
        }
        item->item_order = i;
        item->item_total = mft_item_count;

        // kopírování příslušných fragmentů
        int32_t done = (i * VFS_MFT_FRAGMENTS_COUNT);
        int32_t remaining = fragment_count - done;
        int32_t to_copy = utils_min(VFS_MFT_FRAGMENTS_COUNT, remaining);
        for (int j = 0; j < to_copy; ++j) {
            item->fragments[j].count = fragments[done + j].count;
            item->fragments[j].start_index = fragments[done + j].start_index;
        }
    }

    *out_fragments = fragments;
    *out_fragment_count = fragment_count;
    return RESULT_OK;
}

Result vfs_reallocate(VFS* vfs, MftItem* first_item, int32_t first_item_index, int32_t preferred_min_cluster_index) {
    int32_t size_bytes = first_item->item_size;

    // získání indexů zdrojových clusterů
    int32_t cluster_count = vfs_count_clusters(vfs, first_item);
    int32_t src_clusters[cluster_count];
    vfs_collect_clusters_from_mft_items(vfs, first_item, first_item_index, src_clusters);

    // smazání ostatních mft položek, vyčištění fragmentů první položky
    vfs_clean_content(vfs, first_item, first_item_index, false);

    // alokace nových clusterů, nastavení fragmentů a mft dalších mft položek
    MftFragment *fragments;
    int32_t fragment_count;
    Result initialization = vfs_init_reallocation(
            vfs, first_item, size_bytes, preferred_min_cluster_index, &fragments, &fragment_count);

    if (initialization != RESULT_OK) {
        return RESULT_ERROR;
    }

    // zápis do souboru
    io_update_mft(vfs);
    io_update_bitmap(vfs);

    // přesun dat
    int32_t dest_clusters[cluster_count];
    vfs_collect_clusters_from_mft_items(vfs, first_item, first_item_index, dest_clusters);

    for (int i = 0; i < cluster_count; ++i) {
        io_copy_cluster(vfs, src_clusters[i], dest_clusters[i]);

        // uvolnění starého clusteru
        vfs_bitmap_set(vfs->bitmap, src_clusters[i], false);
    }

    free(fragments);
    return RESULT_OK;
}

void vfs_remove(VFS* vfs, MftItem* first_item, int32_t first_item_index, MftItem* dir) {
    // odstranění položky z adresáře
    vfs_remove_item_from_dir(vfs, dir, first_item->uid);

    // smazání obsahu
    vfs_clean_content(vfs, first_item, first_item_index, true);

    // vyčištění první MFT položky
    vfs_clean_mft_item(vfs, first_item, true, true);
}

void vfs_clean_content(VFS* vfs, MftItem* first_item, int32_t first_item_index, bool change_bitmap) {
    MftItem* mft_items[first_item->item_total];
    vfs_collect_mft_items(vfs, first_item, first_item_index, mft_items);

    // smazání ostatních MFT položek
    for (int i = 1; i < first_item->item_total; ++i) {
        vfs_clean_mft_item(vfs, mft_items[i], true, change_bitmap);
    }

    // vyčištění první MFT položky - pouze obsahu
    vfs_clean_mft_item(vfs, first_item, false, change_bitmap);
    first_item->item_size = 0;
    first_item->item_order = 0;
    first_item->item_total = 1;
}

void vfs_clean_mft_item(VFS* vfs, MftItem* item, bool full, bool change_bitmap) {
    // smazání obsahu - uvolnění clusterů
    for (int i = 0; i < VFS_MFT_FRAGMENTS_COUNT; ++i) {
        MftFragment* fragment = &(item->fragments[i]);
        if (vfs_mft_fragment_is_empty(fragment))
            break;

        if (change_bitmap) {
            vfs_bitmap_set_all(vfs->bitmap, fragment, 1, false);
        }
        fragment->start_index = 0;
        fragment->count = 0;
    }

    if (full) {
        // smazání všech hodnot
        vfs_mft_init_mft_item(item);
    }
}

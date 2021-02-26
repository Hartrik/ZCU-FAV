
/**
 * Implementuje různé pomocné funkce pro práci s vfs.
 *
 * @author: Patrik Harag
 * @version: 2017-12-28
 */

#include <assert.h>
#include "vfs-module-utils.h"
#include "vfs-module-find.h"

int32_t vfs_count_clusters(VFS* vfs, MftItem* item) {
    return utils_div_ceil(item->item_size, vfs->boot_record->cluster_size);
}

int32_t vfs_count_clusters_real(VFS* vfs, MftItem* item) {
    int32_t mft_item_count = item->item_total;
    MftItem* mft_items[mft_item_count];
    vfs_collect_mft_items(vfs, item, -1, mft_items);

    int32_t count = 0;
    for (int i = 0; i < mft_item_count; ++i) {
        MftItem* item = mft_items[i];
        for (int j = 0; j < VFS_MFT_FRAGMENTS_COUNT; ++j) {
            MftFragment* fragment = &item->fragments[j];
            count += fragment->count;
        }
    }
    return count;
}

void vfs_collect_mft_items(VFS* vfs, MftItem* first_item, int32_t first_item_index, MftItem** out_mft_items) {
    int32_t last_index = first_item_index;
    if (first_item->item_total > 1) {
        if (last_index == -1) {
            // nalezení indexu první položky
            vfs_find_item_by_uid(vfs, first_item->uid, 0, &last_index);
        }
        out_mft_items[0] = first_item;

        for (int i = 1; i < first_item->item_total; ++i) {
            MftItem* next_item = vfs_find_item_by_uid(
                    vfs, first_item->uid, last_index + 1, &last_index);

            if (next_item == NULL) {
                printf("Cannot find related MFT items\n");
                break;
            }

            out_mft_items[i] = next_item;
        }
    } else {
        out_mft_items[0] = first_item;
    }
}

void vfs_collect_clusters_from_mft_items(
        VFS* vfs, MftItem* first_item, int32_t first_item_index, int32_t* out_cluster_ids) {

    MftItem* mft_items[first_item->item_total];
    vfs_collect_mft_items(vfs, first_item, first_item_index, mft_items);

    int32_t cluster_index = 0;
    for (int i = 0; i < first_item->item_total; ++i) {
        MftItem* item = mft_items[i];

        for (int j = 0; j < VFS_MFT_FRAGMENTS_COUNT; ++j) {
            MftFragment* fragment = &(item->fragments[j]);

            for (int k = 0; k < fragment->count; ++k) {
                out_cluster_ids[cluster_index++] = fragment->start_index + k;
            }
        }
    }
}

void vfs_collect_clusters_from_fragments(
        MftFragment* fragments, int fragment_count, int32_t* out_cluster_ids) {

    int cluster_index = 0;
    for (int i = 0; i < fragment_count; ++i) {
        MftFragment* fragment = &fragments[i];
        for (int j = 0; j < fragment->count; ++j) {
            out_cluster_ids[cluster_index++] = fragment->start_index + j;
        }
    }
}

int32_t vfs_count_free_space(VFS* vfs) {
    assert(vfs->initialized);
    return vfs_bitmap_count_free_clusters(vfs->bitmap) * vfs->boot_record->cluster_size;
}

void vfs_lookup_table_fill(VFS* vfs, MftItem** out_table) {
    // nastavení všeho na NULL
    for (int i = 0; i < vfs->bitmap->cluster_count; ++i) {
        out_table[i] = NULL;
    }

    // vyplnění pole
    for (int i = 0; i < vfs->mft->length; ++i) {
        MftItem* item = &(vfs->mft->data[i]);
        if (vfs_mft_is_empty(item)) continue;

        for (int j = 0; j < VFS_MFT_FRAGMENTS_COUNT; ++j) {
            MftFragment* fragment = &(item->fragments[j]);
            if (vfs_mft_fragment_is_empty(fragment)) continue;

            for (int k = 0; k < fragment->count; ++k) {
                out_table[fragment->start_index + k] = item;
            }
        }
    }
}

void vfs_lookup_table_update(VFS* vfs, MftItem** out_table,
                             MftItem* first_item, int32_t first_item_index, bool used) {

    MftItem* mft_items[first_item->item_total];
    vfs_collect_mft_items(vfs, first_item, first_item_index, mft_items);

    for (int i = 0; i < first_item->item_total; ++i) {
        MftItem* item = mft_items[i];

        for (int j = 0; j < VFS_MFT_FRAGMENTS_COUNT; ++j) {
            MftFragment* fragment = &(item->fragments[j]);
            if (vfs_mft_fragment_is_empty(fragment)) continue;

            for (int k = 0; k < fragment->count; ++k) {
                out_table[fragment->start_index + k] = (used)
                                                       ? item
                                                       : NULL;
            }
        }
    }
}
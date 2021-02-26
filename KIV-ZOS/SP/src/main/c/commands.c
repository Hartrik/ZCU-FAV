
/**
 * Implementace všech příkazů.
 *
 * @author: Patrik Harag
 * @version: 2017-12-27
 */

#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include "commands.h"
#include "io.h"
#include "vfs-module-find.h"
#include "vfs-module-validation.h"
#include "vfs-module-defragmentation.h"
#include "vfs-module-utils.h"
#include "vfs-module-consistency.h"


Result command_init(ReplState* repl_state, char** argv, int argc) {
    VFS* vfs = repl_state->vfs;

    int32_t disk_size_bytes = VFS_DEFAULT_DISK_SIZE;
    int32_t cluster_size = VFS_DEFAULT_CLUSTER_SIZE;
    int32_t mft_item_count;

    if (argc == 1 || argc == 2) {
        // parametrem je celková velikost disku

        if (argc >= 2) {
            disk_size_bytes = atoi(argv[1]);
        }

        mft_item_count = vfs_mft_count_size(disk_size_bytes);

    } else if (argc == 3) {
        // parametry jsou počet clusterů a počet mft položek

        disk_size_bytes = atoi(argv[1]) * cluster_size;  // samotná data
        mft_item_count = atoi(argv[2]);

    } else {
        // parametry jsou počet clusterů, velikost clusteru, počet mft položek

        cluster_size = atoi(argv[2]);
        disk_size_bytes = atoi(argv[1]) * cluster_size;  // samotná data
        mft_item_count = atoi(argv[3]);
    }

    // validace

    if (disk_size_bytes % cluster_size != 0) {
        printf("Size cannot be divided by cluster size\n");
        return RESULT_WARNING;
    }

    if (cluster_size % 4 != 0) {
        // velikost int32_t
        printf("Cluster size cannot be divided by 4\n");
        return RESULT_WARNING;
    }

    // operaci je možné provést

    // zavřeme aktuální
    io_close(vfs);

    // vytvoříme nový
    MftItem* root = vfs_init(vfs, disk_size_bytes, cluster_size, mft_item_count);
    repl_state->dir = root;

    if (io_create(vfs)) {
        return RESULT_OK;
    }
    return RESULT_ERROR;
}

Result command_df(VFS* vfs) {
    int32_t free = vfs_count_free_space(vfs);
    int32_t total = vfs->boot_record->disk_size;

    printf("Total space [B]: %d\n", total);
    printf("Free space [B]: %d\n", free);
    printf("Used space [B]: %d (%.2f %%)\n",
           total - free, (total - free) / (float) total * 100);

    return RESULT_OK;
}

Result command_show_parameters(VFS* vfs) {
    printf("File: %s\n", vfs->file_name);
    printf("Signature: %s\n", vfs->boot_record->signature);
    printf("Volume descriptor: %s\n", vfs->boot_record->volume_descriptor);
    printf("Disk size [B]: %d\n", vfs->boot_record->disk_size);
    printf("Cluster size [B]: %d\n", vfs->boot_record->cluster_size);
    printf("Cluster count: %d\n", vfs->boot_record->cluster_count);
    printf("Bitmap size [B] %d\n", vfs->boot_record->bitmap_size);
    printf("MFT size [B] %ld\n", vfs->boot_record->mft_item_count * sizeof(MftItem));
    printf("MFT items count %d\n", vfs->boot_record->mft_item_count);
    return RESULT_OK;
}

Result command_show_bitmap(VFS* vfs) {
    const int LINE_BREAK_AFTER = 100;
    int without_line_break = 0;

    for (int i = 0; i < vfs->bitmap->cluster_count; ++i) {
        putchar('0' + vfs_bitmap_is_cluster_used(vfs->bitmap, i));

        without_line_break++;

        if (without_line_break == LINE_BREAK_AFTER) {
            putchar('\n');
            without_line_break = 0;
        }
    }
    putchar('\n');
    return RESULT_OK;
}

Result command_show_mft(VFS* vfs) {
    for (int i = 0; i < vfs->mft->length; ++i) {
        MftItem* item = &(vfs->mft->data[i]);

        if (item->uid == VFS_MFT_UID_ITEM_FREE) {
            printf("%-4d<empty>\n", i);
        } else {
            printf("%-4d%-12d (%d/%d) %-15s %s %8d B  ",
                   i, item->uid, item->item_order + 1, item->item_total, item->item_name,
                   item->is_directory ? "dir " : "file", item->item_size);

            for (int j = 0; j < VFS_MFT_FRAGMENTS_COUNT; ++j) {
                MftFragment* fragment = &item->fragments[j];
                if (!vfs_mft_fragment_is_empty(fragment))
                    printf(" [s=%d, c=%d],", fragment->start_index, fragment->count);
            }
            printf("\n");
        }
    }
    printf("Free MFT items: %d / %d\n",
           vfs_mft_count_free_mft_items(vfs->mft), vfs->boot_record->mft_item_count);
    return RESULT_OK;
}

Result command_pwd(ReplState* repl_state) {
    VFS* vfs = repl_state->vfs;

    char path[REPL_PATH_MAX_LENGTH + 1];  // + \0
    memset(path, 0, REPL_PATH_MAX_LENGTH + 1);

    MftItem* dir = repl_state->dir;
    int index = REPL_PATH_MAX_LENGTH - 1;
    do {
        path[index--] = '/';

        if (dir->parent_uid == VFS_MFT_UID_ITEM_FREE) {
            // root
            break;
        }

        size_t dir_name_len = strlen(dir->item_name);
        if (dir_name_len + 2 > index) {  // + místo pro / a případně ~
            path[index--] = '~';
            break;
        } else {
            index -= dir_name_len;
            strncpy(path + index + 1, dir->item_name, dir_name_len);
        }

        MftItem* next_dir = vfs_find_item_by_uid(vfs, dir->parent_uid, 0, NULL);
        if (next_dir == NULL) {
            printf("Broken reference (parent_uid) uid: %d\n", dir->uid);
            return RESULT_WARNING;
        }
        dir = next_dir;

    } while (dir != NULL);

    printf("%s\n", path + index + 1);
    return RESULT_OK;
}

Result command_cd(ReplState* repl_state, char* path) {
    MftItem* item = vfs_find(repl_state->vfs, path, repl_state->dir);
    if (item == NULL || !item->is_directory) {
        printf("PATH NOT FOUND\n");
        return RESULT_WARNING;

    } else {
        repl_state->dir = item;

        printf("OK\n");
        return RESULT_OK;
    }
}

Result command_ls(ReplState* repl_state, char* path) {
    VFS* vfs = repl_state->vfs;

    MftItem* dir;
    if (strlen(path) == 0 || strcmp(path, ".") == 0) {
        // current dir
        dir = repl_state->dir;
    } else {
        dir = vfs_find(vfs, path, repl_state->dir);

        if (dir == NULL || !dir->is_directory) {
            printf("PATH NOT FOUND\n");
            return RESULT_WARNING;
        }
    }

    if (dir->item_size == 0) {
        // prázdný adresář
        return RESULT_OK;
    }

    // načtení seznamu souborů
    char data[dir->item_size];
    if (!io_load_data_into_buffer(vfs, dir, data)) {
        return RESULT_ERROR;
    }

    int entries = dir->item_size / 4;
    for (int i = 0; i < entries; ++i) {
        int32_t uid = ((int32_t*) data)[i];

        int32_t index;
        MftItem* next_item = vfs_find_item_by_uid(vfs, uid, 0, &index);

        if (next_item == NULL) {
            printf("?%d\n", uid);
        } else {
            printf("%s%s\n", next_item->is_directory ? "+" : "-", next_item->item_name);
        }
    }
    return RESULT_OK;
}

Result command_mkdir(ReplState* repl_state, char* path) {
    VFS* vfs = repl_state->vfs;

    char name[strlen(path) + 1];
    MftItem* dir = vfs_find_parent(vfs, path, repl_state->dir, name);

    if (dir == NULL) {
        printf("PATH NOT FOUND\n");
        return RESULT_WARNING;
    }

    if (!vfs_check_name(name)) {
        printf("FILENAME NOT VALID\n");
        return RESULT_WARNING;
    }

    if (vfs_find_in_dir(vfs, name, dir) != NULL) {
        printf("EXIST\n");
        return RESULT_WARNING;
    }

    MftItem* new_dir = vfs_mkdir(vfs, name, dir);
    if (new_dir == NULL) {
        // nedostatek místa atd...
        return RESULT_WARNING;
    }

    // zápis do souboru
    if (io_update_mft(vfs) && io_update_bitmap(vfs)) {
        printf("OK\n");
        return RESULT_OK;
    }
    return RESULT_ERROR;
}

Result command_rmdir(ReplState* repl_state, char* name) {
    VFS* vfs = repl_state->vfs;

    int32_t fist_item_index;
    MftItem* first_item = vfs_find_with_index(vfs, name, repl_state->dir, &fist_item_index);
    if (first_item == NULL) {
        printf("FILE NOT FOUND\n");
        return RESULT_WARNING;
    }

    if (!first_item->is_directory) {
        printf("NOT DIRECTORY\n");
        return RESULT_WARNING;
    }

    if (first_item->item_size > 0) {
        printf("NOT EMPTY\n");
        return RESULT_WARNING;
    }

    if (first_item->parent_uid == VFS_MFT_UID_ITEM_FREE) {
        printf("ROOT DIRECTORY\n");
        return RESULT_WARNING;
    }

    if (first_item->uid == repl_state->dir->uid) {
        printf("WORKING DIRECTORY\n");
        return RESULT_WARNING;
    }

    MftItem* dir = vfs_find_item_by_uid(vfs, first_item->parent_uid, 0, NULL);
    if (dir == NULL) {
        printf("Cannot find parent dir\n");
        return RESULT_ERROR;
    }

    vfs_remove(vfs, first_item, fist_item_index, dir);

    printf("OK\n");
    return RESULT_OK;
}

Result command_info(ReplState* repl_state, char* path) {
    VFS* vfs = repl_state->vfs;

    int32_t first_item_index;
    MftItem* first_item = vfs_find_with_index(vfs, path, repl_state->dir, &first_item_index);
    if (first_item == NULL) {
        printf("FILE NOT FOUND\n");
        return RESULT_WARNING;
    }
    MftItem* mft_items[first_item->item_total];
    vfs_collect_mft_items(vfs, first_item, first_item_index, mft_items);

    printf("Name: %s\n", first_item->item_name);
    printf("UID: %d\n", first_item->uid);
    printf("Size: %d B\n", first_item->item_size);

    printf("Fragments: ");
    for (int i = 0; i < first_item->item_total; ++i) {
        MftItem* next_item = mft_items[i];
        for (int j = 0; j < VFS_MFT_FRAGMENTS_COUNT; ++j) {
            MftFragment* fragment = &(next_item->fragments[j]);
            if (vfs_mft_fragment_is_empty(fragment)) {
                break;  // not used
            }

            printf("[start=%d, count=%d], ", fragment->start_index, fragment->count);
        }
    }
    printf("\n");

    printf("Clusters: ");
    for (int i = 0; i < first_item->item_total; ++i) {
        MftItem* next_item = mft_items[i];
        for (int j = 0; j < VFS_MFT_FRAGMENTS_COUNT; ++j) {
            MftFragment* fragment = &(next_item->fragments[j]);
            if (vfs_mft_fragment_is_empty(fragment)) {
                break;  // not used
            }

            for (int k = 0; k < fragment->count; ++k) {
                printf("%d, ", fragment->start_index + k);
            }
        }
    }
    printf("\n");
    return RESULT_OK;
}

Result command_incp(ReplState* repl_state, char* src_path, char* dest_path) {
    VFS* vfs = repl_state->vfs;
    FILE* src_file = fopen(src_path, "rb");

    if (src_file == NULL) {
        printf("FILE NOT FOUND\n");
        return RESULT_WARNING;

    } else {
        char name[strlen(dest_path) + 1];
        MftItem* dir = vfs_find_parent(vfs, dest_path, repl_state->dir, name);

        if (!vfs_check_new_item(vfs, dir, name)) {
            return RESULT_WARNING;
        }

        Result r = vfs_create_file(vfs, name, src_file, dir);
        fclose(src_file);

        if (r == RESULT_OK) {
            printf("OK\n");
        }

        return r;
    }
}

Result command_rm(ReplState* repl_state, char* path) {
    VFS* vfs = repl_state->vfs;

    int32_t first_item_index;
    MftItem* first_item = vfs_find_with_index(vfs, path, repl_state->dir, &first_item_index);

    if (first_item == NULL) {
        printf("FILE NOT FOUND\n");
        return RESULT_WARNING;
    }

    if (first_item->is_directory) {
        printf("NOT FILE\n");
        return RESULT_WARNING;
    }

    MftItem* dir = vfs_find_item_by_uid(vfs, first_item->parent_uid, 0, NULL);
    if (dir == NULL) {
        printf("Cannot find parent dir\n");
        return RESULT_ERROR;
    }

    vfs_remove(vfs, first_item, first_item_index, dir);
    if (io_update_mft(vfs) && io_update_bitmap(vfs)) {
        printf("OK\n");
        return RESULT_OK;
    } else {
        return RESULT_ERROR;
    }
}

Result command_mv(ReplState* repl_state, char* src_path, char* dest_path) {
    VFS* vfs = repl_state->vfs;

    MftItem* item = vfs_find(vfs, src_path, repl_state->dir);
    if (item == NULL) {
        printf("FILE NOT FOUND\n");
        return RESULT_WARNING;
    }

    MftItem* old_dir = vfs_find_item_by_uid(vfs, item->parent_uid, 0, NULL);
    if (old_dir == NULL) {
        printf("Cannot find parent dir\n");
        return RESULT_ERROR;
    }

    char new_name[strlen(dest_path) + 1];
    MftItem* new_dir = vfs_find_parent(vfs, dest_path, repl_state->dir, new_name);
    if (!vfs_check_new_item(vfs, new_dir, new_name)) {
        return RESULT_WARNING;
    }

    Result r = vfs_move(vfs, item, new_name, old_dir, new_dir);
    if (r != RESULT_OK) {
        return r;
    }

    if (io_update_mft(vfs)) {
        printf("OK\n");
        return RESULT_OK;
    } else {
        return RESULT_ERROR;
    }
}

Result command_cp(ReplState* repl_state, char* src_path, char* dest_path) {
    VFS* vfs = repl_state->vfs;

    int32_t src_item_index;
    MftItem* src_item = vfs_find_with_index(vfs, src_path, repl_state->dir, &src_item_index);
    if (src_item == NULL) {
        printf("FILE NOT FOUND\n");
        return RESULT_WARNING;
    }

    if (src_item->is_directory) {
        printf("NOT FILE\n");
        return RESULT_WARNING;
    }

    char copy_name[strlen(dest_path) + 1];
    MftItem* copy_dir = vfs_find_parent(vfs, dest_path, repl_state->dir, copy_name);
    if (!vfs_check_new_item(vfs, copy_dir, copy_name)) {
        return RESULT_WARNING;
    }

    Result r = vfs_copy(vfs, src_item, src_item_index, copy_name, copy_dir);
    if (r != RESULT_OK) {
        return r;
    }

    if (io_update_mft(vfs) && io_update_bitmap(vfs)) {
        printf("OK\n");
        return RESULT_OK;
    } else {
        return RESULT_ERROR;
    }
}

Result command_cat(ReplState* repl_state, char* path) {
    VFS* vfs = repl_state->vfs;

    MftItem* first_item = vfs_find(vfs, path, repl_state->dir);
    if (first_item == NULL || first_item->is_directory) {
        printf("FILE NOT FOUND\n");
        return RESULT_WARNING;
    }

    io_load_data_into_file(vfs, first_item, stdout);

    return RESULT_OK;
}

Result command_outcp(ReplState* repl_state, char* src_path, char* dest_path) {
    VFS* vfs = repl_state->vfs;

    MftItem* first_item = vfs_find(vfs, src_path, repl_state->dir);
    if (first_item == NULL || first_item->is_directory) {
        printf("FILE NOT FOUND\n");
        return RESULT_WARNING;
    }

    FILE* out_file = fopen(dest_path, "wb");
    if (out_file == NULL) {
        printf("PATH NOT FOUND");
        return RESULT_WARNING;
    }

    Result r;
    if (io_load_data_into_file(vfs, first_item, out_file)) {
        printf("OK\n");
        r = RESULT_OK;
    } else {
        r = RESULT_WARNING;
    }
    fclose(out_file);

    return r;
}

Result command_reallocate(ReplState* repl_state, char* path) {
    VFS* vfs = repl_state->vfs;

    int32_t item_index;
    MftItem* item = vfs_find_with_index(vfs, path, repl_state->dir, &item_index);
    if (item == NULL) {
        printf("FILE NOT FOUND\n");
        return RESULT_WARNING;
    }

    return vfs_reallocate(vfs, item, item_index, 0);
}

Result command_defragment(ReplState* repl_state) {
    Result r = vfs_defragment(repl_state->vfs);
    if (r != RESULT_OK) {
        return r;
    }

    if (io_update_mft(repl_state->vfs) && io_update_bitmap(repl_state->vfs)) {
        printf("OK\n");
        return RESULT_OK;
    } else {
        return RESULT_ERROR;
    }
}

Result command_check_consistency(VFS* vfs) {
    if (vfs_check_consistency(vfs)) {
        printf("OK\n");
        return RESULT_OK;
    } else {
        return RESULT_ERROR;
    }
}
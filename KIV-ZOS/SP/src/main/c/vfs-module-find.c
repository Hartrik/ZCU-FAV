
/**
 * Implementuje funkce pro vyhledávání MFT položek.
 *
 * @author: Patrik Harag
 * @version: 2017-12-23
 */

#include <stdio.h>
#include <assert.h>
#include "vfs-module-find.h"
#include "io.h"


MftItem* vfs_find_root(VFS* vfs, int32_t* out_index) {
    assert(vfs->initialized);

    Mft* mft = vfs->mft;
    for (int i = 0; i < mft->length; ++i) {
        MftItem* item = &(mft->data[i]);
        if (item->uid != VFS_MFT_UID_ITEM_FREE
            && item->is_directory
            && strcmp(item->item_name, "/") == 0) {

            if (out_index != NULL) {
                *out_index = i;
            }
            return item;
        }
    }
    return NULL;
}

MftItem* vfs_find_item_by_uid(VFS* vfs, int32_t uid, int32_t from_index,
                              int32_t *out_index) {

    for (int i = from_index; i < vfs->mft->length; ++i) {
        MftItem* item = &(vfs->mft->data[i]);
        if (item->uid == uid) {
            if (out_index != NULL) {
                *out_index = i;
            }
            return item;
        }
    }
    return NULL;
}

int32_t vfs_find_item_index_if_needed(VFS* vfs, MftItem* item) {
    if (item->item_total == 1) {
        return -1;  // není potřeba
    }
    int32_t index;
    vfs_find_item_by_uid(vfs, item->uid, 0, &index);
    return index;
}

MftItem* vfs_find_with_index(VFS* vfs, char* path, MftItem* current_dir,
                             int32_t* out_item_index) {

    if (strlen(path) == 0) {
        // lomítko na konci: xx/yy/ == xx/yy
        return current_dir;
    }

    const char *ptr = strchr(path, '/');
    if (ptr) {
        int index = (int) (ptr - path);

        if (index == 0) {
            // absolutní cesta
            return vfs_find_with_index(vfs, path+1, vfs_find_root(vfs, out_item_index), out_item_index);
        } else {
            // získáme z cesty první část

            char first_part[index + 1];
            first_part[index] = '\0';
            memcpy(first_part, path, (size_t) index);

            MftItem* next_item = vfs_find_in_dir(vfs, first_part, current_dir);
            if (next_item == NULL || !next_item->is_directory) {
                return NULL;
            }

            return vfs_find_with_index(vfs, path + index + 1, next_item, out_item_index);
        }
    } else {
        // neobsahuje '/' => jeden název
        return vfs_find_in_dir_with_index(vfs, path, current_dir, out_item_index);
    }
}

MftItem* vfs_find(VFS* vfs, char* path, MftItem* current_dir) {
    return vfs_find_with_index(vfs, path, current_dir, NULL);
}

MftItem* vfs_find_parent(VFS* vfs, char* path, MftItem* current_dir, char* out_name) {
    if (strlen(path) == 0) {
        return NULL;
    }

    const char *ptr = strchr(path, '/');
    if (ptr) {
        int index = (int) (ptr - path);

        if (index == 0) {
            // absolutní cesta
            return vfs_find_parent(vfs, path+1, vfs_find_root(vfs, NULL), out_name);
        } else {
            // získáme z cesty první část

            char first_part[index + 1];
            first_part[index] = '\0';
            memcpy(first_part, path, (size_t) index);

            MftItem* next_item = vfs_find_in_dir(vfs, first_part, current_dir);
            if (next_item == NULL || !next_item->is_directory) {
                return NULL;
            }

            return vfs_find_parent(vfs, path + index + 1, next_item, out_name);
        }
    } else {
        // neobsahuje '/' => jeden název

        size_t len = strlen(path);
        memcpy(out_name, path, (size_t) len);
        out_name[len] = '\0';

        return current_dir;
    }
}

MftItem* vfs_find_in_dir_with_index(VFS *vfs, char *name, MftItem *dir,
                                    int32_t *out_item_index) {
    assert(dir->is_directory);

    if (dir->item_size == 0) {
        // adresář je prázdný
        return NULL;
    }

    // načtení seznamu souborů
    char data[dir->item_size];
    if (!io_load_data_into_buffer(vfs, dir, data)) {
        return NULL;
    }

    int entries = dir->item_size / 4;
    for (int i = 0; i < entries; ++i) {
        int32_t uid = ((int32_t*) data)[i];

        MftItem* next_item = vfs_find_item_by_uid(vfs, uid, 0, out_item_index);
        if (next_item != NULL) {
            if (strcmp(name, next_item->item_name) == 0) {
                return next_item;
            }
        }
    }
    return NULL;
}

MftItem* vfs_find_in_dir(VFS* vfs, char* name, MftItem* dir) {
    return vfs_find_in_dir_with_index(vfs, name, dir, NULL);
}

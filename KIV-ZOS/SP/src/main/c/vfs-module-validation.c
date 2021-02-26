
/**
 * Implementuje různé validační funkce.
 *
 * @author: Patrik Harag
 * @version: 2017-12-20
 */

#include <stdio.h>
#include "vfs-module-validation.h"
#include "vfs-module-find.h"


bool vfs_check_name(char* name) {
    size_t len = strlen(name);

    if (len == 0)
        return false;

    for (int i = 0; i < len; ++i) {
        char c = name[i];
        if (c == '/') return false;
    }
    return true;
}

bool vfs_check_new_item(VFS* vfs, MftItem* new_item_dir, char* new_item_name) {
    if (new_item_dir == NULL) {
        printf("PATH NOT FOUND\n");
        return false;
    }

    if (!vfs_check_name(new_item_name)) {
        printf("FILENAME NOT VALID\n");
        return false;
    }

    if (vfs_find_in_dir(vfs, new_item_name, new_item_dir) != NULL) {
        printf("EXIST\n");
        return false;
    }
    return true;
}
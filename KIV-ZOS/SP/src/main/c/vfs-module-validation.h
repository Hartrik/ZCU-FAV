
/**
 * Definuje různé validační funkce.
 *
 * @author: Patrik Harag
 * @version: 2017-12-20
 */

#ifndef KIV_ZOS_VFS_MODULE_VALIDATION_H
#define KIV_ZOS_VFS_MODULE_VALIDATION_H

#include "vfs.h"


/** Zkontroluje jméno položky. */
bool vfs_check_name(char* name);
/** Zkontroluje zda je možné vytvořit nový soubor */
bool vfs_check_new_item(VFS* vfs, MftItem* new_item_dir, char* new_item_name);

#endif

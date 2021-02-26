
/**
 * Definuje funkce pro vyhledávání MFT položek.
 *
 * @author: Patrik Harag
 * @version: 2017-12-23
 */

#ifndef KIV_ZOS_VFS_MODULE_FIND_H
#define KIV_ZOS_VFS_MODULE_FIND_H

#include "vfs.h"


/** Najde kořenovou složku */
MftItem* vfs_find_root(VFS* vfs, int32_t* out_index);

/**
 * Najde první položku od určitého indexu v MFT podle UID.
 *
 * @param vfs
 * @param uid
 * @param from_index index na kterém za začne hledat
 * @param out_index pointer kterému bude předán index nalezené položky,
 *                  může být NULL
 * @return první položka nebo NULL
 */
MftItem* vfs_find_item_by_uid(VFS* vfs, int32_t uid, int32_t from_index, int32_t *out_index);

/**
 * Pokud je položka složena z více MFT položek, tak vrátí index první položky.
 * Jinak vrátí -1. Pokud totiž procházíme položky, tak potřebujeme znát index
 * té předchozí.
 *
 * @param vfs
 * @param item první položka
 * @return index první položky
 */
int32_t vfs_find_item_index_if_needed(VFS* vfs, MftItem* item);

/** Najde položku podle cesty */
MftItem* vfs_find(VFS* vfs, char* path, MftItem* current_dir);

/** Najde položku podle cesty */
MftItem* vfs_find_with_index(VFS* vfs, char* path, MftItem* current_dir, int32_t* out_item_index);

/**
 * Najde adresář, ve kterém je položka (nemusí existovat), na kterou ukazuje
 * zadaná cesta.
  *
  * @param vfs
  * @param path
  * @param current_dir
  * @param out_name pointer na řetězec, do kterého umístí název položky
  * @return adresář
  */
MftItem* vfs_find_parent(VFS* vfs, char* path, MftItem* current_dir, char* out_name);

/** Najde položku v daném adresáři podle jména */
MftItem* vfs_find_in_dir(VFS* vfs, char* name, MftItem* dir);

/** Najde položku v daném adresáři podle jména */
MftItem* vfs_find_in_dir_with_index(VFS *vfs, char *name, MftItem *dir, int32_t *out_item_index);

#endif

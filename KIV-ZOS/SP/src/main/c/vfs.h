
/**
 * Definuje základní funkce pro práci s vfs.
 *
 * @author: Patrik Harag
 * @version: 2017-12-25
 */

#ifndef KIV_ZOS_VFS_H
#define KIV_ZOS_VFS_H

#include <stdio.h>
#include <stdint.h>
#include <stdbool.h>
#include <string.h>
#include "vfs-mft.h"
#include "vfs-boot-record.h"
#include "vfs-bitmap.h"
#include "utils.h"

#define VFS_DEFAULT_DISK_SIZE (1024 * 10)
#define VFS_DEFAULT_CLUSTER_SIZE 256

// struktury

/** Uchovává všechny důležité informace o vfs */
typedef struct _VFS {
    char* file_name;
    FILE* file;
    bool initialized;

    BootRecord* boot_record;
    Mft* mft;
    Bitmap* bitmap;
} VFS;

// funkce

/** Vytvoří struktutu, kterou je nutné ještě inicializovat */
VFS* vfs_create(char* file_name);
/** Uvolní strukturu */
void vfs_free(VFS* vfs);

/** Inicializuje virtuální filesystem */
MftItem* vfs_init(VFS* vfs, int32_t disk_size, int32_t cluster_size, int32_t mft_size);
/** Vytvoří kořenový adresář */
MftItem* vfs_create_root_dir(VFS* vfs);

/** Vytvoří soubor */
Result vfs_create_file(VFS* vfs, char* name, FILE* src_file, MftItem* dir);
/** Změní obsah souboru */
Result vfs_change_content(VFS* vfs, MftItem* first_item, char* data, int32_t size_bytes);
/** Přesune soubor nebo složku */
Result vfs_move(VFS* vfs, MftItem* item, char* new_name, MftItem* old_dir, MftItem* new_dir);
/** Zkopíruje soubor */
Result vfs_copy(VFS* vfs, MftItem* src_item, int32_t src_item_index, char* copy_name, MftItem* copy_dir);

/** Odstraní soubor nebo složku */
void vfs_remove(VFS* vfs, MftItem* first_item, int32_t first_item_index, MftItem* dir);

/** Vytvoří složku */
MftItem* vfs_mkdir(VFS* vfs, char* name, MftItem* dir);
/** Přidá položku do složky */
Result vfs_add_item_into_dir(VFS *vfs, MftItem *dir, int32_t uid);
/** Odebere položku ze složky */
Result vfs_remove_item_from_dir(VFS *vfs, MftItem *dir, int32_t uid);

/** Realokuje soubor */
Result vfs_reallocate(VFS* vfs, MftItem* first_item, int32_t first_item_index, int32_t preferred_min_cluster_index);

/** Smaže obsah položky, první položku zanechá */
void vfs_clean_content(VFS* vfs, MftItem* first_item, int32_t first_item_index, bool change_bitmap);
/** Smaže jednu MFT položku */
void vfs_clean_mft_item(VFS* vfs, MftItem* item, bool full, bool change_bitmap);

/** Připraví mft pro vytvoření nového souboru */
Result vfs_init_new_file(VFS* vfs, int32_t uid, char* name, long size_bytes, MftItem* dir,
                         MftFragment** out_fragments, int32_t* out_fragment_count);

/** Připraví mft pro vytvoření nového souboru realokací */
Result vfs_init_reallocation(VFS* vfs, MftItem* first_item, int32_t size_bytes,
                             int32_t preferred_min_cluster_index,
                             MftFragment** out_fragments, int32_t* out_fragment_count);

#endif

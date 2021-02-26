
/**
 * Definuje nízkoúrovňové funkce pro práci s boot record.
 *
 * @author: Patrik Harag
 * @version: 2017-11-14
 */

#ifndef KIV_ZOS_VFS_BOOT_RECORD_H
#define KIV_ZOS_VFS_BOOT_RECORD_H

#include <stdint.h>

// struktury

/** Struktura boot record tak, jak se bude ukládat do souboru */
typedef struct _BootRecord {
    char signature[16];              // login autora FS
    char volume_descriptor[48];      // popis vygenerovaného FS
    int32_t disk_size;               // maximální velikost obsahu
    int32_t cluster_size;            // velikost clusteru
    int32_t cluster_count;           // pocet clusteru
    int32_t mft_item_count;          // počet položek v mft
    int32_t bitmap_size;             // velikost bitmapy v bytech
} BootRecord;

// funkce

/** Inicializuje strukturu */
void vfs_boot_record_init(BootRecord *boot_record, int32_t disk_size,
                          int32_t cluster_size, int32_t mft_size);

/** Uvolní strukturu */
void vfs_boot_record_free(BootRecord *boot_record);

#endif

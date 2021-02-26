
/**
 * Definuje funkce pro práci s IO.
 *
 * @author: Patrik Harag
 * @version: 2017-12-21
 */

#include "vfs.h"

#ifndef KIV_ZOS_IO_H
#define KIV_ZOS_IO_H

/** Otestuje, zda soubor s vfs existuje */
bool io_exists(VFS* vfs);
/** Otevře soubor s vfs */
bool io_open(VFS* vfs);
/** Zavře soubor s vfs */
void io_close(VFS* vfs);
/** Naformátuje soubor s vfs */
bool io_create(VFS *vfs);
/** Načte informace o vfs ze souboru - boot record, mft, bitmap */
bool io_load(VFS *vfs);

/** Načte data souboru ve vfs a zapíše je do souboru */
bool io_load_data_into_file(VFS *vfs, MftItem *first_item, FILE *out_file);
/** Načte data souboru ve vfs a uloží je do bufferu */
bool io_load_data_into_buffer(VFS* vfs, MftItem* first_item, char* data);

/** Zapíše data ze souboru mimo vfs do určených fragmentů */
bool io_write_data_from_file(VFS* vfs, MftFragment* fragments, int32_t fragment_count,
                             FILE* src_file, int32_t size);
/** Zapíše data z bufferu do určených fragmentů */
bool io_write_data_from_buffer(VFS* vfs, MftFragment* fragments, int32_t fragment_count,
                               char* data, int32_t data_count);
/** Zkopíruje data z clusteru na určenou pozici */
bool io_copy_cluster(VFS* vfs, int32_t src_cluster_index, int32_t dest_cluster_index);

/** Zapíše MFT do souboru */
bool io_update_mft(VFS* vfs);
/** Zapíše bitmapu do souboru */
bool io_update_bitmap(VFS* vfs);

/** Vrátí index, kde v souboru začíná sekce s MFT */
long long io_get_mft_section_start(VFS* vfs);
/** Vrátí index, kde v souboru začíná sekce s bitmapou */
long long io_get_bitmap_section_start(VFS* vfs);
/** Vrátí index, kde v souboru začíná sekce s daty */
long long io_get_data_section_start(VFS* vfs);

#endif

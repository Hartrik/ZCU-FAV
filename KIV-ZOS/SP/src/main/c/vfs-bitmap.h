
/**
 * Definuje nízkoúrovňové funkce pro práci s bitmapou.
 *
 * @author: Patrik Harag
 * @version: 2017-12-22
 */

#ifndef KIV_ZOS_VFS_BITMAP_H
#define KIV_ZOS_VFS_BITMAP_H

#include <stdint.h>
#include "vfs-mft.h"

// struktury

/** Struktura pro reprezentaci bitmapy */
typedef struct _Bitmap {
    int32_t length;
    unsigned char* data;
    int32_t cluster_count;
} Bitmap;

// funkce

/** Inicializuje strukturu */
void vfs_bitmap_init(Bitmap* bitmap, int32_t bytes);
/** Uvolní strukturu */
void vfs_bitmap_free(Bitmap* bitmap);

/** Spočte počet volných clusterů */
int32_t vfs_bitmap_count_free_clusters(Bitmap* bitmap);

/**
 * Najde určitý počet volných clusterů v jedom kuse - v jednom fragmentu.
 *
 * @param bitmap
 * @param n požadovaná velikost
 * @return pole mft fragmentů nebo NULL
 */
MftFragment* vfs_bitmap_find_free_clusters_first_fit(
        Bitmap* bitmap, int32_t n, int32_t preferred_min_cluster_index);

/**
 * Najde určitý počet volných clusterů tak, jak jdou za sebou.
 *
 * @param bitmap
 * @param n požadovaná velikost
 * @param
 * @return pole mft fragmentů nebo NULL
 */
MftFragment* vfs_bitmap_find_free_clusters_take_first(
        Bitmap* bitmap, int32_t n, int32_t preferred_min_cluster_index, int32_t* out_count);

/** Nastaví všechny clustery ze zadaných fragmentů jako volné nebo použité */
void vfs_bitmap_set_all(Bitmap* bitmap, MftFragment* fragments, int fragment_count, bool used);
/** Nastaví cluster jako volný nebo použitý */
void vfs_bitmap_set(Bitmap* bitmap, int cluster_index, bool used);

/** Informuje, zda je cluster použit */
bool vfs_bitmap_is_cluster_used(Bitmap* bitmap, int32_t cluster_index);

#endif


/**
 * Definuje funkce, které umožňují alokovat a uvolňovat různé zdroje.
 *
 * @author: Patrik Harag
 * @version: 2017-12-28
 */

#ifndef KIV_ZOS_VFS_MODULE_ALLOCATION_H
#define KIV_ZOS_VFS_MODULE_ALLOCATION_H

#include "vfs.h"


extern bool DISABLE_FIRST_FIT;

/** Najde určitý počet volných clusterů. Nemění bitmapu! */
MftFragment* vfs_find_free_clusters(VFS *vfs, int32_t n,
        int32_t preferred_min_cluster_index, int32_t *fragment_count);
/** Alokuje clustery podle zadané velikosti */
bool vfs_allocate_clusters(VFS *vfs, long size_bytes_l,
       int32_t preferred_min_cluster_index, MftFragment** out_fragments, int32_t* out_fragment_count);
/** Uvolní clustery */
void vfs_release_clusters(VFS *vfs, MftFragment* fragments, int count);

/** Alokuje MFT položky */
bool vfs_allocate_mft_items(VFS *vfs, int32_t n, MftItem** out_items, int32_t uid);
/** Uvolní MFT položky */
void vfs_release_mft_items(VFS *vfs, MftItem** items, int32_t n);

#endif

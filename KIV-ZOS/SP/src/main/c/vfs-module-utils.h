
/**
 * Definuje různé pomocné funkce pro práci s vfs.
 *
 * @author: Patrik Harag
 * @version: 2017-12-28
 */

#ifndef KIV_ZOS_VFS_MODULE_UTILS_H
#define KIV_ZOS_VFS_MODULE_UTILS_H

#include "vfs.h"

/** Sesbírá všechny MFT položky jedenoho souboru nebo složky dojednoho pole */
void vfs_collect_mft_items(VFS* vfs, MftItem* first_item, int32_t first_item_index, MftItem** out_mft_items);
/** Sesbírá všechny clustery jedenoho souboru nebo složky dojednoho pole */
void vfs_collect_clusters_from_mft_items(VFS* vfs, MftItem* first_item, int32_t first_item_index, int32_t* out_cluster_ids);
/** Sesbírá všechny clustery jedenoho souboru nebo složky dojednoho pole */
void vfs_collect_clusters_from_fragments(MftFragment* fragments, int fragment_count, int32_t* out_cluster_ids);

/** Spočítá počet clusterů které zabírá jeden soubor nebo složka */
int32_t vfs_count_clusters(VFS* vfs, MftItem* item);
/** Spočítá počet clusterů které zabírá jeden soubor nebo složka, skutečně je projde */
int32_t vfs_count_clusters_real(VFS* vfs, MftItem* item);

/** Spočítá volné místo */
int32_t vfs_count_free_space(VFS* vfs);

/** Vytvoří tabulku clusterů: index => MftItem*  */
void vfs_lookup_table_fill(VFS* vfs, MftItem** out_table);
/** Aktualizuje tabulku o určitou položku */
void vfs_lookup_table_update(VFS* vfs, MftItem** out_table,
                             MftItem* first_item, int32_t first_item_index, bool used);

#endif


/**
 * Implementuje funkce pro defragmentaci.
 *
 * @author: Patrik Harag
 * @version: 2017-12-23
 */

#include "vfs-module-defragmentation.h"
#include "vfs-module-utils.h"
#include "vfs-module-find.h"
#include "io.h"


/** Najde první neprázdný cluster */
static int32_t vfs_defragment_find_first_cluster(
        VFS* vfs, MftItem** lookup_table, int32_t start) {

    for (int i = start; i < vfs->bitmap->cluster_count; ++i) {
        if (lookup_table[i] != NULL)
            return i;
    }
    return -1;
}

Result vfs_defragment(VFS* vfs) {
    // vytvoření tabulky clusterů: index => MftItem*
    MftItem* lookup_table[vfs->bitmap->cluster_count];
    vfs_lookup_table_fill(vfs, lookup_table);

    int32_t next_cluster_to_process = 0;
    while (true) {

        // najdeme první neprázdný cluster a podle něj určíme další položku,
        // kterou budeme defragmetovat
        int32_t next_non_empty_cluster = vfs_defragment_find_first_cluster(
                vfs, lookup_table, next_cluster_to_process);

        if (next_non_empty_cluster < 0) {
            // defragmentováno
            break;
        }

        // během defragmetace této položky budou informace o jejích clusterech
        // uložené v mft neaktuální!
        MftItem* next_item = lookup_table[next_non_empty_cluster];
        int32_t next_item_index = vfs_find_item_index_if_needed(vfs, next_item);
        int32_t next_item_size = next_item->item_size;
        int32_t next_item_cluster_count = vfs_count_clusters(vfs, next_item);
        int32_t next_item_clusters[next_item_cluster_count];
        vfs_collect_clusters_from_mft_items(vfs, next_item, next_item_index, next_item_clusters);

        if (next_cluster_to_process == next_non_empty_cluster
                && vfs_mft_is_not_fragmented(next_item)) {

            // už je to defragmentované...
            next_cluster_to_process += next_item_cluster_count;
            continue;
        }

        // půjdeme cluster po clusteru a budeme je přesouvat na správné místo
        for (int i = 0; i < next_item_cluster_count; ++i) {
            int32_t dest_cluster = next_cluster_to_process + i;
            MftItem* dest_current_item = lookup_table[dest_cluster];

            if (dest_current_item != NULL) {
                // někdo tam je

                if (dest_current_item->uid == next_item->uid) {
                    // jsou tam správná data..
                    continue;

                } else {
                    // zajistíme, aby byl cluster uvolněn
                    // - realokujeme celý soubor někam pryč

                    int32_t dest_current_item_index = vfs_find_item_index_if_needed(
                            vfs, dest_current_item);

                    vfs_lookup_table_update(vfs, lookup_table,
                            dest_current_item, dest_current_item_index, false);

                    vfs_reallocate(vfs, dest_current_item, dest_current_item_index,
                            next_cluster_to_process + next_item_cluster_count);

                    vfs_lookup_table_update(
                            vfs, lookup_table, dest_current_item, dest_current_item_index, true);
                }
            }

            // nakopírujeme cluster a ten starý uvolníme
            int32_t src_cluster = next_item_clusters[i];
            io_copy_cluster(vfs, src_cluster, dest_cluster);

            vfs_bitmap_set(vfs->bitmap, src_cluster, false);
            vfs_bitmap_set(vfs->bitmap, dest_cluster, true);
            lookup_table[dest_cluster] = next_item;
            lookup_table[src_cluster] = NULL;
        }

        // smazání ostatních mft položek, vyčištění fragmentů první položky
        vfs_clean_content(vfs, next_item, next_item_index, false);
        // nastavení nového fragmentu
        next_item->item_size = next_item_size;
        next_item->fragments[0].start_index = next_cluster_to_process;
        next_item->fragments[0].count = next_item_cluster_count;

        next_cluster_to_process += next_item_cluster_count;
    }

    return RESULT_OK;
}
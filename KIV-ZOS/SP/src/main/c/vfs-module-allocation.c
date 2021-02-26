
/**
 * Implementuje funkce, které umožňují alokovat a uvolňovat různé zdroje.
 *
 * @author: Patrik Harag
 * @version: 2017-12-28
 */

#include <stdio.h>
#include <assert.h>
#include "vfs-module-allocation.h"
#include "vfs-module-utils.h"


bool DISABLE_FIRST_FIT = false;

MftFragment* vfs_find_free_clusters(VFS *vfs, int32_t n,
        int32_t preferred_min_cluster_index, int32_t *fragment_count) {

    if (n == 0) {
        *fragment_count = 0;
        return NULL;
    }

    MftFragment* fragments;

    if (!DISABLE_FIRST_FIT) {
        // prvně se pokusíme najít clustery za sebou
        fragments = vfs_bitmap_find_free_clusters_first_fit(vfs->bitmap, n, preferred_min_cluster_index);
        if (fragments != NULL) {
            *fragment_count = 1;
            return fragments;
        }
    }

    // když nenajdeme clustery za sebou, vezmeme je jak přijdou
    fragments = vfs_bitmap_find_free_clusters_take_first(
            vfs->bitmap, n, preferred_min_cluster_index, fragment_count);

    if (fragments != NULL) {
        return fragments;
    }

    assert(false);
}

bool vfs_allocate_clusters(VFS *vfs, long size_bytes_l, int32_t preferred_min_cluster_index,
                           MftFragment** out_fragments, int32_t* out_fragment_count) {

    if (size_bytes_l > vfs_count_free_space(vfs)) {
        printf("NOT ENOUGH SPACE\n");
        return false;
    }

    // nemůže přetéct... viz podmínka výše
    int32_t size_bytes = (int32_t) size_bytes_l;

    // potřebné clustery
    int32_t clusters = utils_div_ceil(size_bytes, vfs->boot_record->cluster_size);

    // pokus o alokaci
    *out_fragments = vfs_find_free_clusters(vfs, clusters, preferred_min_cluster_index, out_fragment_count);

    if (*out_fragments == NULL && size_bytes != 0) {
        // teď to zkusíme kdekoliv
        *out_fragments = vfs_find_free_clusters(vfs, clusters, 0, out_fragment_count);
    }

    if (*out_fragments == NULL && size_bytes != 0) {
        printf("NOT ENOUGH SPACE\n");
        return false;
    } else {
        // update bitmapy
        vfs_bitmap_set_all(vfs->bitmap, *out_fragments, *out_fragment_count, true);
    }
    return true;
}

void vfs_release_clusters(VFS *vfs, MftFragment* fragments, int count) {
    // update bitmapy
    vfs_bitmap_set_all(vfs->bitmap, fragments, count, false);
}

bool vfs_allocate_mft_items(VFS *vfs, int32_t n, MftItem** out_items, int32_t uid) {
    if (vfs_mft_find_free_mft_items(vfs->mft, n, out_items)) {
        for (int i = 0; i < n; ++i) {
            out_items[i]->uid = uid;
        }
        return true;
    }
    return (n == 0);
}

void vfs_release_mft_items(VFS *vfs, MftItem** items, int32_t n) {
    for (int i = 0; i < n; ++i) {
        vfs_mft_init_mft_item(items[i]);
    }
}

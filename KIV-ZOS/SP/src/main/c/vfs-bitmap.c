
/**
 * Implementuje nízkoúrovňové funkce pro práci s bitmapou.
 *
 * @author: Patrik Harag
 * @version: 2017-12-22
 */

#include <stdlib.h>
#include "vfs-bitmap.h"
#include "utils.h"


void vfs_bitmap_init(Bitmap* bitmap, int32_t cluster_count) {
    bitmap->cluster_count = cluster_count;
    bitmap->length = utils_div_ceil(cluster_count, 8);
    bitmap->data = (unsigned char*) calloc((size_t) bitmap->length, sizeof(char));
}

void vfs_bitmap_free(Bitmap* bitmap) {
    free(bitmap->data);
    bitmap->length = -1;
    bitmap->cluster_count = -1;
}

int32_t vfs_bitmap_count_free_clusters(Bitmap* bitmap) {
    int32_t free_clusters = 0;
    for (int i = 0; i < bitmap->cluster_count; ++i) {
        if (!vfs_bitmap_is_cluster_used(bitmap, i))
            free_clusters++;
    }
    return free_clusters;
}

/**
 * Vrací počet volných clusterů od určitého clusteru (včetně) dál.
 *
 * @param bitmap
 * @param max maximální velikost, dále už nehledá
 * @return počet volných clusterů
 */
static int32_t vfs_bitmap_fragment_size(Bitmap* bitmap, int32_t start, int32_t max) {
    int32_t n = 0;
    for (int32_t i = start; i < bitmap->cluster_count && n < max; ++i) {
        if (vfs_bitmap_is_cluster_used(bitmap, i)) {
            break;
        } else {
            n++;
        }
    }
    return n;
}

MftFragment* vfs_bitmap_find_free_clusters_first_fit(
        Bitmap* bitmap, int32_t n, int32_t preferred_min_cluster_index) {

    int32_t i = preferred_min_cluster_index;
    while (i < bitmap->cluster_count) {
        int32_t row = vfs_bitmap_fragment_size(bitmap, i, n);

        if (row == n) {
            // máme to

            MftFragment* fragment = (MftFragment *) calloc(1, sizeof(MftFragment));
            fragment->count = n;
            fragment->start_index = i;
            return fragment;

        } else {
            // posuneme index
            i += row + 1;  // plus ten obsazený za nimi
        }
    }

    return NULL;
}

MftFragment* vfs_bitmap_find_free_clusters_take_first(
        Bitmap* bitmap, int32_t n, int32_t preferred_min_cluster_index, int32_t* out_count) {

    MftFragment* fragments;
    int32_t fragment_count = 0;
    int32_t cluster_count = 0;

    int32_t i = preferred_min_cluster_index;
    while (i < bitmap->cluster_count) {
        int32_t row = vfs_bitmap_fragment_size(bitmap, i, n - cluster_count);

        if (row != 0) {
            if (fragment_count == 0) {
                fragments = (MftFragment *) calloc(1, sizeof(MftFragment));
            } else {
                fragments = (MftFragment *) realloc(fragments, (fragment_count + 1) * sizeof(MftFragment));
            }

            fragments[fragment_count].count = row;
            fragments[fragment_count].start_index = i;

            cluster_count += row;
            fragment_count += 1;

            if (cluster_count == n) {
                *out_count = fragment_count;
                return fragments;
            }
        }
        // posuneme index
        i += row + 1;
    }

    return NULL;
}

void vfs_bitmap_set_all(Bitmap* bitmap, MftFragment* fragments, int fragment_count, bool used) {
    for (int i = 0; i < fragment_count; ++i) {
        MftFragment* fragment = &fragments[i];

        for (int j = 0; j < fragment->count; ++j) {
            int cluster_index = j + fragment->start_index;

            vfs_bitmap_set(bitmap, cluster_index, used);
        }
    }
}

void vfs_bitmap_set(Bitmap* bitmap, int cluster_index, bool used) {
    int byte_index = cluster_index / 8;

    unsigned char value = bitmap->data[byte_index];
    int pos = 7 - (cluster_index % 8);

    unsigned char new_value = (used)
          ? utils_set_bit(value, pos)
          : utils_unset_bit(value, pos);

    bitmap->data[byte_index] = new_value;
}

bool vfs_bitmap_is_cluster_used(Bitmap* bitmap, int32_t cluster_index) {
    unsigned char b = bitmap->data[cluster_index / 8];
    return utils_is_bit_set(b, 7 - (cluster_index % 8));
}

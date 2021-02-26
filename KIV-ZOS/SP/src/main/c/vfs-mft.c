
/**
 * Implementuje nízkoúrovňové funkce pro práci s MFT.
 *
 * @author: Patrik Harag
 * @version: 2017-12-20
 */

#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <assert.h>
#include "vfs-mft.h"


int32_t vfs_mft_count_size(int32_t disk_size) {
    int32_t mft_size = (int32_t)(disk_size * VFS_MFT_DEFAULT_SIZE_RATIO);

    // zaokrouhlení na velikost jedné položky v mft
    mft_size = mft_size - (mft_size % sizeof(MftItem));

    // na bajty
    mft_size = mft_size / sizeof(MftItem);

    return mft_size + 1;
}

void vfs_mft_init_mft_item(MftItem* item) {
    memset(item, 0, sizeof(MftItem));

    item->uid = VFS_MFT_UID_ITEM_FREE;
    item->parent_uid = VFS_MFT_UID_ITEM_FREE;
}

void vfs_mft_init(Mft* mft, int32_t size) {
    mft->data = (MftItem *) calloc((size_t) size, sizeof(MftItem));
    mft->length = size;

    for (int i = 0; i < size; ++i) {
        vfs_mft_init_mft_item(&(mft->data[i]));
    }
}

void vfs_mft_free(Mft* mft) {
    free(mft->data);
    mft->length = -1;
}

int32_t vfs_mft_count_free_mft_items(Mft* mft) {
    int32_t n = 0;
    for (int i = 0; i < mft->length; ++i) {
        if (mft->data[i].uid == VFS_MFT_UID_ITEM_FREE)
            n++;
    }

    return n;
}

bool vfs_mft_find_free_mft_items(Mft* mft, int32_t n, MftItem** out_items) {
    int found = 0;
    for (int i = 0; i < mft->length && found < n; ++i) {
        MftItem* item = &(mft->data[i]);
        if (item->uid == VFS_MFT_UID_ITEM_FREE) {
            out_items[found] = item;

            found++;
        }
    }

    return (found == n);
}

static bool vfs_mft_is_uid_used(Mft* mft, int32_t uid) {
    if (uid == VFS_MFT_UID_ITEM_FREE)
        return true;

    for (int i = 0; i < mft->length; ++i)
        if (mft->data[i].uid == uid)
            return true;

    return false;
}

int32_t vfs_mft_generate_random_uid(Mft* mft) {
    int32_t uid;
    do {
        uid = rand();

    } while (vfs_mft_is_uid_used(mft, uid));

    return uid;
}

bool vfs_mft_is_not_fragmented(MftItem* item) {
    int32_t used_fragments = 0;
    for (int i = 0; i < VFS_MFT_FRAGMENTS_COUNT; ++i) {
        if (!vfs_mft_fragment_is_empty(&item->fragments[i]))
            used_fragments++;
    }

    return item->item_total == 1 && used_fragments <= 1;
}

bool vfs_mft_is_empty(MftItem* item) {
    return item->uid == VFS_MFT_UID_ITEM_FREE;
}

bool vfs_mft_fragment_is_empty(MftFragment* fragment) {
    return fragment->start_index == 0 && fragment->count == 0;
}

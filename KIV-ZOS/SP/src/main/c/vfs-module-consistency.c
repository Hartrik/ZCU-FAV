
/**
 * Implementuje funkce pro ověřování konzistence vfs.
 *
 * @author: Patrik Harag
 * @version: 2017-12-27
 */

#include <pthread.h>
#include <stdlib.h>
#include "vfs-module-consistency.h"
#include "vfs-module-utils.h"

#define THREADS 2


/**
 * Vrátí další položku ke zpracování.
 *
 * @param vfs
 * @param mutex
 * @param i
 * @return položka nebo NULL
 */
static MftItem* _next_item(VFS* vfs, pthread_mutex_t* mutex, int32_t* i) {
    pthread_mutex_lock(mutex);

    MftItem* result = NULL;
    while (*i < vfs->mft->length && result == NULL) {
        MftItem* item = &vfs->mft->data[*i];

        if (!vfs_mft_is_empty(item) && item->item_order == 0) {
            result = item;
        }

        *i = *i + 1;
    }

    pthread_mutex_unlock(mutex);

    return result;
}

/**
 * Funkce, která je zavolána po vytvoření vlákna.
 *
 * @param context_void_ptr pointer na sdílenou strukutu s daty
 */
static void* _check_handler(void* context_void_ptr) {
    CheckContext* context = (CheckContext*) context_void_ptr;

    VFS* vfs = context->vfs;
    MftItem* item;

    while ((item = _next_item(vfs, &context->mutex, &context->i)) != NULL) {
        int32_t cluster_count = vfs_count_clusters_real(vfs, item);
        int32_t expected = vfs_count_clusters(vfs, item);

        // test očekávaného počtu clusterů
        if (cluster_count != expected) {
            // reportuje nalezenou nekonzistenci
            pthread_mutex_lock(&context->mutex);

            context->consistent = false;
            printf("Consistency error: item %d, clusters=%d, expected=%d\n",
                   item->uid, cluster_count, expected);

            pthread_mutex_unlock(&context->mutex);
        }

        // nalezení všech clusterů
        int32_t clusters[cluster_count];
        vfs_collect_clusters_from_mft_items(vfs, item, -1, clusters);

        // test jestli není nějaká cluster referencován více položkami
        pthread_mutex_lock(&context->mutex);

        for (int i = 0; i < cluster_count; ++i) {
            int32_t cluster_id = clusters[i];
            if (context->clusters[cluster_id] == NULL) {
                context->clusters[cluster_id] = item;
            } else {
                // nekonzistence
                context->consistent = false;
                printf("Consistency error: cluster %d is used by item %d and %d\n",
                       cluster_id, context->clusters[cluster_id]->uid, item->uid);
            }
        }

        pthread_mutex_unlock(&context->mutex);
    }

    return NULL;
}

bool vfs_check_consistency(VFS* vfs) {
    // inicializace sdílené struktury s daty
    CheckContext* context = malloc(sizeof(CheckContext));
    context->vfs = vfs;
    context->i = 0;
    context->consistent = true;
    context->clusters = calloc((size_t) vfs->bitmap->cluster_count, sizeof(MftItem*));

    if (pthread_mutex_init(&context->mutex, NULL) != 0) {
        perror("Mutex init failed");
        return false;
    }

    // vytvoření a spuštění vláken
    pthread_t threads[THREADS];
    for (int i = 0; i < THREADS; ++i) {
        pthread_create(&threads[i], NULL, _check_handler, context);
    }

    // čekání na dokončení
    for (int i = 0; i < THREADS; ++i) {
        pthread_join(threads[i], NULL);
    }

    bool result = context->consistent;
    pthread_mutex_destroy(&context->mutex);
    free(context->clusters);
    free(context);
    return result;
}

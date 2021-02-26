
/**
 * Definuje funkce pro ověřování konzistence vfs.
 *
 * @author: Patrik Harag
 * @version: 2017-12-27
 */

#ifndef KIV_ZOS_VFS_MODULE_CONSISTENCY_H
#define KIV_ZOS_VFS_MODULE_CONSISTENCY_H

#include "vfs.h"

/** Struktura, která udržuje sdílená data, která potřebují všechna vlákna */
typedef struct _CheckContext {
    VFS* vfs;
    int32_t i;
    bool consistent;
    pthread_mutex_t mutex;
    MftItem** clusters;

} CheckContext;

/**
 * Zkontroluje zda velikosti souborů odpovídají počtu jejich clusterů a jestli
 * není cluster referencován více položkami.
 *
 * @param vfs
 * @return
 */
bool vfs_check_consistency(VFS* vfs);

#endif


/**
 * Definuje funkce pro defragmentaci.
 *
 * @author: Patrik Harag
 * @version: 2017-12-22
 */

#ifndef KIV_ZOS_VFS_MODULE_DEFRAGMENTATION_H
#define KIV_ZOS_VFS_MODULE_DEFRAGMENTATION_H

#include "vfs.h"


/**
 * Provede úplnou defragmentaci vfs.
 *
 * @param vfs virtuální fs
 * @return výsldek operace
 */
Result vfs_defragment(VFS* vfs);

#endif

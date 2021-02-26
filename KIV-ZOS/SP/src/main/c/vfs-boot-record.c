
/**
 * Implementuje nízkoúrovňové funkce pro práci s boot record.
 *
 * @author: Patrik Harag
 * @version: 2017-12-20
 */

#include <string.h>
#include "vfs-boot-record.h"
#include "utils.h"

void vfs_boot_record_init(BootRecord *boot_record, int32_t disk_size,
                          int32_t cluster_size, int32_t mft_size) {

    memset(boot_record, 0, sizeof(BootRecord));

    strncpy(boot_record->signature, "user", sizeof(boot_record->signature) - 1);
    strncpy(boot_record->volume_descriptor, "virtual file system", sizeof(boot_record->volume_descriptor) - 1);

    boot_record->disk_size = disk_size;
    boot_record->cluster_size = cluster_size;
    boot_record->cluster_count = disk_size / cluster_size;

    boot_record->mft_item_count = mft_size;
    boot_record->bitmap_size = utils_div_ceil(boot_record->cluster_count, 8);
}

void vfs_boot_record_free(BootRecord *boot_record) {
    // nic
}

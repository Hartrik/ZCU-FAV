
/**
 * Implementuje funkce pro práci s IO.
 *
 * @author: Patrik Harag
 * @version: 2017-12-21
 */

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include "io.h"
#include "vfs-module-find.h"
#include "vfs-module-consistency.h"


bool io_exists(VFS* vfs) {
    FILE* file = fopen(vfs->file_name, "r");
    if (file != NULL) {
        fclose(file);
        return true;
    }
    return false;
}

bool io_open(VFS* vfs) {
    if (vfs->file != NULL) {
        printf("The file is opened already.");
        return true;
    }

    FILE* vfs_file = fopen(vfs->file_name, "r+b");
    if (vfs_file == NULL) {
        printf("Cannot open virtual file system file '%s'\n", vfs->file_name);
        return false;
    } else {
        vfs->file = vfs_file;
        return true;
    }
}

void io_close(VFS* vfs) {
    if (vfs->file != NULL) {
        fclose(vfs->file);
        vfs->file = NULL;
    }
}

bool io_create(VFS* vfs) {
    FILE* vfs_file = fopen(vfs->file_name, "wb");

    if (vfs_file == NULL) {
        printf("Cannot create new virtual file system '%s'\n", vfs->file_name);
        return false;

    } else {
        // fragmentace

        // boot record
        fwrite(vfs->boot_record, sizeof(BootRecord), 1, vfs_file);

        // mft
        fwrite(vfs->mft->data, sizeof(MftItem), (size_t) vfs->mft->length, vfs_file);

        // bitmapa
        fwrite(vfs->bitmap->data, sizeof(char), (size_t) vfs->bitmap->length, vfs_file);

        // clustery
        int32_t cluster_size = vfs->boot_record->cluster_size;
        int32_t cluster_count = vfs->boot_record->cluster_count;
        char zeros[cluster_size];
        memset(zeros, 0, (size_t) cluster_size);

        for (int i = 0; i < cluster_count; ++i) {
            fwrite(zeros, (size_t) cluster_size, 1, vfs_file);
        }

        fclose(vfs_file);
    }

    return io_open(vfs);
}

bool io_load_data_into_file(VFS *vfs, MftItem *first_item, FILE *out_file) {
    long data_start = io_get_data_section_start(vfs);
    char buffer[vfs->boot_record->cluster_size];
    int32_t to_send = first_item->item_size;

    int32_t last_item = -1;
    for (int i = 0; i < first_item->item_total; ++i) {
        MftItem* next_item;
        if (i == 0) {
            // první položku už máme
            next_item = first_item;
        } else {
            if (last_item == -1) {
                // potřebujeme index předchozí položky
                vfs_find_item_by_uid(vfs, first_item->uid, 0, &last_item);
            }

            next_item = vfs_find_item_by_uid(
                    vfs, first_item->uid, last_item + 1, &last_item);
        }

        if (next_item == NULL) {
            printf("Cannot find related MFT items\n");
            break;
        }

        for (int j = 0; j < VFS_MFT_FRAGMENTS_COUNT; ++j) {
            MftFragment* fragment = &(next_item->fragments[j]);
            if (vfs_mft_fragment_is_empty(fragment)) {
                break;  // not used
            }

            for (int k = 0; k < fragment->count; ++k) {
                int cluster_index = fragment->start_index + k;
                int cluster_start_rel = cluster_index * vfs->boot_record->cluster_size;

                fseek(vfs->file, data_start + cluster_start_rel, SEEK_SET);
                fread(&buffer, vfs->boot_record->cluster_size, 1, vfs->file);

                int32_t size = utils_min(to_send, vfs->boot_record->cluster_size);
                to_send -= size;

                fwrite(&buffer, (size_t) size, 1, out_file);
            }
        }
    }
    return true;
}

bool io_write_data_from_file(
        VFS* vfs, MftFragment* fragments, int32_t fragment_count, FILE* src_file, int32_t count) {

    long data_start = io_get_data_section_start(vfs);
    char buffer[vfs->boot_record->cluster_size];
    int32_t done = 0;

    for (int i_fragment = 0; i_fragment < fragment_count; ++i_fragment) {
        MftFragment* fragment = &fragments[i_fragment];

        for (int i_cluster = 0; i_cluster < fragment->count; ++i_cluster) {
            int cluster_start = (fragment->start_index + i_cluster) * vfs->boot_record->cluster_size;

            fseek(vfs->file, data_start + cluster_start, SEEK_SET);
            int32_t size = utils_min(vfs->boot_record->cluster_size, count - done);

            fread(&buffer, (size_t) size, 1, src_file);
            fwrite(&buffer, (size_t) size, 1, vfs->file);

            done += size;
        }
    }
    fflush(vfs->file);
    return true;
}

bool io_load(VFS* vfs) {
    if (!io_open(vfs)) {
        return false;
    }

    FILE* vfs_file = vfs->file;
    assert(vfs->file != NULL && vfs->initialized == false);

    // boot record
    fread(vfs->boot_record, sizeof(BootRecord), 1, vfs_file);

    // mft
    int mft_size = vfs->boot_record->mft_item_count;
    vfs_mft_init(vfs->mft, mft_size);
    fread(vfs->mft->data, sizeof(MftItem), (size_t) mft_size, vfs_file);

    // bitmapa
    int bitmap_size_bytes = vfs->boot_record->bitmap_size;
    vfs_bitmap_init(vfs->bitmap, vfs->boot_record->cluster_count);
    fread(vfs->bitmap->data, sizeof(char), (size_t) bitmap_size_bytes, vfs_file);

    // clustery
    // ... dynamicky...

    // test konzistence
    if (vfs_check_consistency(vfs)) {
        vfs->initialized = true;
        return true;
    }
    return false;
}

bool io_load_data_into_buffer(VFS* vfs, MftItem* first_item, char* data) {
    FILE* vfs_file = vfs->file;
    assert(vfs->file != NULL && vfs->initialized == true);

    long data_start = io_get_data_section_start(vfs);
    char buffer[vfs->boot_record->cluster_size];
    int32_t to_send = first_item->item_size;

    int32_t last_item = -1;
    for (int i = 0; i < first_item->item_total; ++i) {
        MftItem* next_item;
        if (i == 0) {
            // první položku už máme
            next_item = first_item;
        } else {
            if (last_item == -1) {
                // potřebujeme index předchozí položky
                vfs_find_item_by_uid(vfs, first_item->uid, 0, &last_item);
            }

            next_item = vfs_find_item_by_uid(
                    vfs, first_item->uid, last_item + 1, &last_item);
        }

        if (next_item == NULL) {
            printf("Cannot find related MFT items\n");
            break;
        }

        for (int j = 0; j < VFS_MFT_FRAGMENTS_COUNT; ++j) {
            MftFragment* fragment = &(next_item->fragments[j]);
            if (vfs_mft_fragment_is_empty(fragment)) {
                break;  // not used
            }

            for (int k = 0; k < fragment->count; ++k) {
                int cluster_index = fragment->start_index + k;
                int cluster_start_rel = cluster_index * vfs->boot_record->cluster_size;

                fseek(vfs_file, data_start + cluster_start_rel, SEEK_SET);
                fread(&buffer, (size_t) vfs->boot_record->cluster_size, 1, vfs_file);

                int32_t size = utils_min(to_send, vfs->boot_record->cluster_size);
                memcpy(data + first_item->item_size - to_send, buffer, (size_t) size);

                to_send -= size;
            }
        }
    }
    return true;
}

bool io_write_data_from_buffer(VFS* vfs, MftFragment* fragments, int32_t fragment_count,
                               char* data, int32_t data_count) {

    long data_start = io_get_data_section_start(vfs);
    int32_t done = 0;

    for (int i_fragment = 0; i_fragment < fragment_count; ++i_fragment) {
        MftFragment* fragment = &fragments[i_fragment];

        for (int i_cluster = 0; i_cluster < fragment->count; ++i_cluster) {
            int cluster_start = (fragment->start_index + i_cluster) * vfs->boot_record->cluster_size;

            int cluster_size = vfs->boot_record->cluster_size;
            size_t size = (size_t) utils_min(cluster_size, data_count - done);

            fseek(vfs->file, data_start + cluster_start, SEEK_SET);
            fwrite(data + done, size, 1, vfs->file);

            done += size;
        }
    }
    fflush(vfs->file);
    return true;
}

bool io_copy_cluster(VFS* vfs, int32_t src_cluster_index, int32_t dest_cluster_index) {
    long data_start = io_get_data_section_start(vfs);

    int32_t cluster_size = vfs->boot_record->cluster_size;
    char buffer[cluster_size];

    int src_cluster_start = src_cluster_index * cluster_size;
    fseek(vfs->file, data_start + src_cluster_start, SEEK_SET);
    fread(&buffer, (size_t) cluster_size, 1, vfs->file);

    int dest_cluster_start = dest_cluster_index * cluster_size;
    fseek(vfs->file, data_start + dest_cluster_start, SEEK_SET);
    fwrite(&buffer, (size_t) cluster_size, 1, vfs->file);

    fflush(vfs->file);
    return true;
}

long long io_get_mft_section_start(VFS* vfs) {
    return sizeof(BootRecord);
}

long long io_get_bitmap_section_start(VFS* vfs) {
    return io_get_mft_section_start(vfs) + sizeof(MftItem) * vfs->boot_record->mft_item_count;
}

long long io_get_data_section_start(VFS* vfs) {
    return io_get_bitmap_section_start(vfs) + vfs->boot_record->bitmap_size;
}

bool io_update_mft(VFS* vfs) {
    FILE* vfs_file = vfs->file;
    assert(vfs->file != NULL && vfs->initialized == true);

    fseek(vfs_file, io_get_mft_section_start(vfs), SEEK_SET);
    fwrite(vfs->mft->data, sizeof(MftItem), (size_t) vfs->mft->length, vfs_file);
    fflush(vfs_file);
    return true;
}

bool io_update_bitmap(VFS* vfs) {
    FILE* vfs_file = vfs->file;
    assert(vfs->file != NULL && vfs->initialized == true);

    fseek(vfs_file, io_get_bitmap_section_start(vfs), SEEK_SET);
    fwrite(vfs->bitmap->data, sizeof(char), (size_t) vfs->bitmap->length, vfs_file);
    fflush(vfs_file);
    return true;
}

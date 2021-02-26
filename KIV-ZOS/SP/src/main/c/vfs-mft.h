
/**
 * Definuje nízkoúrovňové funkce pro práci s MFT.
 *
 * @author: Patrik Harag
 * @version: 2017-12-22
 */

#ifndef KIV_ZOS_VFS_MFT_H
#define KIV_ZOS_VFS_MFT_H

#define VFS_MFT_DEFAULT_SIZE_RATIO 0.1
#define VFS_MFT_FRAGMENTS_COUNT 4
#define VFS_MFT_UID_ITEM_FREE 0

#define VFS_MFT_ITEM_NAME_LENGTH 12  //8+3 + /0


#include <stdint.h>
#include <stdbool.h>

// struktury

/** Struktura fragmentu MFT položky tak, jak se bude ukládat do souboru */
typedef struct _MftFragment {
    int32_t start_index;     // start adresa
    int32_t count;           // pocet clusteru ve fragmentu
} MftFragment;

/** Struktura MFT položky tak, jak se bude ukládat do souboru */
typedef struct _MftItem {
    int32_t uid;             // UID polozky, pokud UID = UID_ITEM_FREE, je polozka volna
    int32_t parent_uid;
    int8_t is_directory;     // soubor, nebo adresar
    int8_t item_order;       // poradi v MFT pri vice souborech, jinak 1
    int8_t item_total;       // celkovy pocet polozek v MFT
    char item_name[VFS_MFT_ITEM_NAME_LENGTH];
    int32_t item_size;       // velikost souboru v bytech
    MftFragment fragments[VFS_MFT_FRAGMENTS_COUNT]; //fragmenty souboru
} MftItem;

/** Pomocná struktura pro MFT */
typedef struct _MFT {
    int32_t length;
    MftItem* data;  // pole struktur
} Mft;

// funkce

/** Spočte velikost MFT podle velikosti disku a VFS_MFT_DEFAULT_SIZE_RATIO */
int32_t vfs_mft_count_size(int32_t disk_size);

/** Inicializuje strukturu */
void vfs_mft_init(Mft* mft, int32_t size);
/** Inicializuje strukturu */
void vfs_mft_init_mft_item(MftItem* item);
/** Uvolní strukturu */
void vfs_mft_free(Mft* mft);

/** Spočte volné MFT položky */
int32_t vfs_mft_count_free_mft_items(Mft* mft);
/** Najde volné MFT položky */
bool vfs_mft_find_free_mft_items(Mft *mft, int32_t n, MftItem **out_items);
/** Vygeneruje náhodné UID */
int32_t vfs_mft_generate_random_uid(Mft* mft);

/** Otestuje MFT položku, zda není fragmentovaná */
bool vfs_mft_is_not_fragmented(MftItem* item);
/** Otestuje MFT položku, zda je volná */
bool vfs_mft_is_empty(MftItem* item);
/** Otestuje fragment, zda je volný */
bool vfs_mft_fragment_is_empty(MftFragment* fragment);

#endif

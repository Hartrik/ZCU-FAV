
/**
 * Definice všech příkazů.
 *
 * @author: Patrik Harag
 * @version: 2017-12-27
 */

#include "vfs.h"
#include "utils.h"
#include "repl.h"

#ifndef KIV_ZOS_COMMANDS_H
#define KIV_ZOS_COMMANDS_H

/** Inicializuje virtuální fs */
Result command_init(ReplState* repl_state, char** argv, int argc);

/** Vypíše informace o obsazenosti fs */
Result command_df(VFS* vfs);
/** Vypíše parametry fs */
Result command_show_parameters(VFS* vfs);
/** Zobrazí bitmapu; 0 => volný cluster, 1 => obsazený cluster */
Result command_show_bitmap(VFS* vfs);
/** Vypíše MFT položky */
Result command_show_mft(VFS* vfs);

/** Vypíše aktuální cestu */
Result command_pwd(ReplState* repl_state);
/** Změní aktuální cestu */
Result command_cd(ReplState* repl_state, char* path);
/** Vypíše seznam položek v adresáři */
Result command_ls(ReplState* repl_state, char* path);
/** Vytvoří nový adresář */
Result command_mkdir(ReplState* repl_state, char* path);
/** Smaže adresář */
Result command_rmdir(ReplState* repl_state, char* path);
/** Nahraje soubor do virtuálního fs */
Result command_incp(ReplState* repl_state, char* src_path, char* dest_path);
/** Přesune soubor nebo složku */
Result command_mv(ReplState* repl_state, char* src_path, char* dest_path);
/** Zkopíruje soubor */
Result command_cp(ReplState* repl_state, char* src_path, char* dest_path);
/** Smaže soubor */
Result command_rm(ReplState* repl_state, char* path);
/** Vypíše informace o položce */
Result command_info(ReplState* repl_state, char* path);
/** Vypíše obsah souboru */
Result command_cat(ReplState* repl_state, char* path);
/** Nahraje soubor z virtuálního fs do hostitelského fs */
Result command_outcp(ReplState* repl_state, char* src_path, char* dest_path);

/** Realokuje soubor na jinou pozici - to ho může defragmentovat */
Result command_reallocate(ReplState* repl_state, char* path);
/** Defragmentuje celý virtuální fs */
Result command_defragment(ReplState* repl_state);
/** Zkontroluje konzistenci virtuálního fs */
Result command_check_consistency(VFS* vfs);

#endif

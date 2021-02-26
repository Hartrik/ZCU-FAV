
/**
 * Definuje funkce CLI.
 *
 * @author: Patrik Harag
 * @version: 2017-12-20
 */

#ifndef KIV_ZOS_REPL_H
#define KIV_ZOS_REPL_H

#include "vfs.h"
#include "utils.h"

#define REPL_MAX_INPUT_SIZE 512

#define REPL_CONSOLE_OK       0
#define REPL_CONSOLE_NO_INPUT 1
#define REPL_CONSOLE_TOO_LONG 2

#define REPL_PATH_MAX_LENGTH 128

extern bool REPL_TEST;

/** Struktura, která udržuje stav v jakém se nachází REPL. */
typedef struct _ReplState {
    VFS* vfs;
    MftItem* dir;
} ReplState;

/** Spusti read–eval–print loop */
Result repl_start(VFS* vfs);

/** Vyhodnotí textový vstup, který obsahuje příkazy */
Result repl_eval(ReplState* repl_state, char** input, int cont);
/** Vyhodnotí soubor s příkazy */
Result repl_eval_script(ReplState* repl_state, char* script_file);

/** Inicializuje strukturu */
void repl_state_init(ReplState *repl_state);
/** Uvolní strukturu */
void repl_state_free(ReplState *repl_state);

#endif

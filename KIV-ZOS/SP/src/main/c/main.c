
/**
 * Soubor se vstupním bodem programu.
 *
 * @author: Patrik Harag
 * @version: 2017-12-17
 */

#include <stdio.h>
#include <stdlib.h>
#include "vfs.h"
#include "repl.h"

/**
 * Vstupní funkce.
 *
 * @param argc počet argumentů
 * @param argv argumenty
 * @return exit code
 */
int main(int argc, char* argv[]) {
    srand(42);

    char* file_name = NULL;

    for (int i = 1; i < argc; ++i) {
        char* par = argv[i];
        if (strcmp("-t", par) == 0) {
            REPL_TEST = true;
        } else {
            file_name = par;
        }
    }

    if (file_name == NULL) {
        printf("Filename not set!\n");
        exit(EXIT_FAILURE);
    }

    VFS* vfs = vfs_create(file_name);
    Result return_code = repl_start(vfs);

    vfs_free(vfs);
    free(vfs);

    return return_code == RESULT_EXIT ? RESULT_OK : return_code;
}
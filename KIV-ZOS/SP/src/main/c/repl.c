
/**
 * Implementuje funkce CLI.
 *
 * @author: Patrik Harag
 * @version: 2017-12-27
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include "repl.h"
#include "io.h"
#include "commands.h"
#include "vfs-module-find.h"
#include "vfs-module-allocation.h"

bool REPL_TEST = false;

/**
 * Nacte z konzole dalsi radek.
 *
 * @param buffer
 * @param buffer_size
 * @return uspesnost operace
 */
static int repl_console_read_line (char *buffer, int buffer_size) {
    int c, extra;

    /* nacteni vstupu do bufferu */
    if (fgets (buffer, buffer_size, stdin) == NULL)
        return REPL_CONSOLE_NO_INPUT;

    if (buffer[strlen(buffer) - 1] != '\n') {
        extra = 0;
        while (((c = getchar()) != '\n') && (c != EOF))
            extra = 1;

        return (extra == 1) ? REPL_CONSOLE_TOO_LONG : REPL_CONSOLE_OK;
    }

    /* nahrazení \n koncen retezce */
    buffer[strlen(buffer) - 1] = '\0';
    return REPL_CONSOLE_OK;
}

/**
 * Pokusí se načíst vfs ze souboru.
 *
 * @param repl_state
 * @return uspesnost operace
 */
static Result repl_load_vfs(ReplState* repl_state) {
    // načte boot record, mft a bitmapu
    Result return_code = io_load(repl_state->vfs) ? RESULT_OK : RESULT_ERROR;

    if (return_code == RESULT_OK) {
        MftItem* root = vfs_find_root(repl_state->vfs, NULL);
        if (root != NULL) {
            repl_state->dir = root;
        } else {
            printf("ROOT NOT FOUND\n");
            return_code = RESULT_ERROR;
        }
    }
    return return_code;
}

Result repl_start(VFS* vfs) {
    char buff[REPL_MAX_INPUT_SIZE];

    ReplState repl_state;
    repl_state_init(&repl_state);
    repl_state.vfs = vfs;

    Result return_code = RESULT_OK;

    if (io_exists(vfs)) {
        return_code = repl_load_vfs(&repl_state);
    }

    bool show_dollar = true;
    while (return_code == RESULT_OK || return_code == RESULT_WARNING) {
        if (show_dollar) {
            printf("\n$ ");
            fflush(stdout);
        } else {
            show_dollar = true;
        }

        int console_return_code = repl_console_read_line(buff, REPL_MAX_INPUT_SIZE);

        if (console_return_code == REPL_CONSOLE_NO_INPUT) {
            // no input
            return_code = RESULT_EXIT;
            continue;
        }

        if (console_return_code == REPL_CONSOLE_TOO_LONG) {
            printf("Input too long\n");
            return_code = RESULT_WARNING;
            continue;
        }

        if (buff[0] == 0) {
            /* nezadan zadny vstup */
            show_dollar = false;
            continue;
        }

        if (REPL_TEST) {
            printf("%s\n", buff);
        }

        int argc;
        char** argv = utils_split_by_space(buff, &argc);
        return_code = repl_eval(&repl_state, argv, argc);
        free(argv);
    }

    io_close(vfs);
    repl_state_free(&repl_state);

    return return_code;
}

void repl_state_init(ReplState *repl_state) {
    memset(repl_state, 0, sizeof(ReplState));
}

void repl_state_free(ReplState *repl_state) {

}

Result repl_eval(ReplState* repl_state, char** input, int count) {
    assert(count > 0);

    VFS* vfs = repl_state->vfs;
    char* command = input[0];

    // příkazy, které je možné provést bez inicializovaného vfs

    if (strcmp(command, "init") == 0) {
        return command_init(repl_state, input, count);

    } else if (strcmp(command, "load") == 0) {
        // Načte soubor z pevného disku, ve kterém budou jednotlivé příkazy a
        // začne je sekvenčně vykonávat. Formát je 1 příkaz/1řádek
        // load s1

        assert(count == 2);
        return repl_eval_script(repl_state, input[1]);

    } else if (strcmp(command, "disable") == 0) {
        assert(count == 2);

        char* sub_command = input[1];

        if (strcmp(sub_command, "first-fit") == 0) {
            DISABLE_FIRST_FIT = true;
            printf("OK\n");
            return RESULT_OK;

        } else {
            printf("Unknown parameter\n");
            return RESULT_WARNING;
        }
    }

    if (vfs->initialized == false) {
        printf("File system is not initialized\n");
        printf("Command: init <size in bytes>\n");
        return RESULT_WARNING;
    }

    // příkazy, které je možné provést pouze s inicializovaným vfs

    if (strcmp(command, "show") == 0) {
        assert(count == 2);

        char* sub_command = input[1];

        if (strcmp(sub_command, "parameters") == 0) {
            return command_show_parameters(vfs);

        } else if (strcmp(sub_command, "bitmap") == 0) {
            return command_show_bitmap(vfs);

        } else if (strcmp(sub_command, "mft") == 0) {
            return command_show_mft(vfs);

        } else {
            printf("Unknown parameter\n");
            return RESULT_WARNING;
        }

    } else if (strcmp(command, "df") == 0) {
        return command_df(vfs);

    } else if (strcmp(command, "info") == 0) {
        // Vypíše informace o souboru/adresáři s1/a1
        // (v jakých fragmentech/clusterech se nachází), uid, ...
        // info s1/a1

        assert(count == 2);
        return command_info(repl_state, input[1]);

    } else if (strcmp(command, "pwd") == 0) {
        return command_pwd(repl_state);

    } else if (strcmp(command, "cd") == 0) {
        assert(count == 2);
        return command_cd(repl_state, input[1]);

    } else if (strcmp(command, "ls") == 0) {
        assert(count == 1 || count == 2);
        return command_ls(repl_state, (count == 1) ? "" : input[1]);

    } else if (strcmp(command, "mkdir") == 0) {
        assert(count == 2);
        return command_mkdir(repl_state, input[1]);

    } else if (strcmp(command, "rmdir") == 0) {
        assert(count == 2);
        return command_rmdir(repl_state, input[1]);

    } else if (strcmp(command, "rm") == 0) {
        // Smaže soubor s1
        // rm s1

        assert(count == 2);
        return command_rm(repl_state, input[1]);

    } else if (strcmp(command, "mv") == 0) {
        // Přesune soubor s1 do umístění s2
        // mv s1 s2

        assert(count == 3);
        return command_mv(repl_state, input[1], input[2]);

    } else if (strcmp(command, "cp") == 0) {
        // Zkopíruje soubor s1 do umístění s2
        // cp s1 s2

        assert(count == 3);
        return command_cp(repl_state, input[1], input[2]);

    } else if (strcmp(command, "incp") == 0) {
        // Nahraje soubor s1 z pevného disku do umístění s2 v pseudoNTFS
        // incp s1 s2

        assert(count == 3);
        return command_incp(repl_state, input[1], input[2]);

    } else if (strcmp(command, "outcp") == 0) {
        // Nahraje soubor s1 z pseudoNTFS do umístění s2 na pevném disku
        // outcp s1 s2

        assert(count == 3);
        return command_outcp(repl_state, input[1], input[2]);

    } else if (strcmp(command, "cat") == 0) {
        // Vypíše obsah souboru s1
        // cat s1

        assert(count == 2);
        return command_cat(repl_state, input[1]);

    } else if (strcmp(command, "defragment") == 0) {
        return command_defragment(repl_state);

    } else if (strcmp(command, "reallocate") == 0) {
        assert(count == 2);
        return command_reallocate(repl_state, input[1]);

    } else if (strcmp(command, "check") == 0) {
        assert(count == 2);

        char* sub_command = input[1];

        if (strcmp(sub_command, "consistency") == 0) {
            return command_check_consistency(vfs);

        } else {
            printf("Unknown parameter\n");
            return RESULT_WARNING;
        }

    } else if (strcmp(command, "exit") == 0 || strcmp(command, "quit") == 0) {
        return RESULT_EXIT;

    } else {
        printf("UNKNOWN COMMAND: '%s'\n", command);
    }

    return RESULT_OK;
}

Result repl_eval_script(ReplState* repl_state, char* script_file) {
    FILE *file = fopen(script_file, "r");
    if (file == NULL) {
        printf("FILE NOT FOUND\n");
        return RESULT_WARNING;
    }

    Result exit_status = RESULT_OK;
    char buffer[128];  // max délka řádky
    while (fgets(buffer, sizeof(buffer), file) != NULL) {
        // odstanění new-line
        size_t len = strlen(buffer);
        if (buffer[len - 1] == '\n')
            buffer[len - 1] = '\0';

        int argc;
        char** argv = utils_split_by_space(buffer, &argc);

        if (argc > 0) {
            // tisk příkazu
            printf("\n>>>");
            for (int i = 0; i < argc; ++i) {
                printf(" %s", argv[i]);
            }
            printf("\n");

            exit_status = repl_eval(repl_state, argv, argc);
            free(argv);
        }

        if (!(exit_status == RESULT_OK || exit_status == RESULT_WARNING))
            break;
    }
    fclose(file);

    return exit_status;
}

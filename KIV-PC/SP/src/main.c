/*
 * File:   main.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-03
 */

#include <stdio.h>
#include <stdlib.h>

#include "commons.h"
#include "types.h"
#include "repl.h"
#include "interpreter.h"


/**
 * Vstupni metoda.
 */
int main(int argc, char** argv) {
    int exit_code;

    if (argc <= 1) {
        /* REPL */
        interpreter_init();
        exit_code = repl_start();
        interpreter_shutdown();

    } else {
        /* nacteni ze souboru */
        char *file_name = argv[1];

        interpreter_init();
        exit_code = repl_eval_file(file_name);
        interpreter_shutdown();
    }

    if (DEBUG) {
        printf("\n------------- DEBUG --------------\n");
        printf("Allocated objects: %d", get_allocated_objects());
        printf("\n----------------------------------\n");
    }

    return exit_code;
}
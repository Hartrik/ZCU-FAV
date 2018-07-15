/*
 * File:   repl.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-06
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include "commons.h"
#include "types.h"
#include "lexer.h"
#include "repl.h"
#include "console.h"
#include "interpreter.h"
#include "reader.h"


/**
 * Kontroluje vyslednou hodnotu, zda neznaci konec programu.
 */
static boolean has_quit_code(Value *v) {
    return (v != NULL) && (v->type == TYPE_EXCEPTION)
            && strcmp(as_exception(v), REPL_QUIT_CODE) == 0;
}

/**
 * Kontroluje seznam hodnot, zda nektera z nich neznaci konec programu.
 */
static boolean contains_quit_code(ValueList *list) {
    int i;

    if (list == NULL)
        return false;

    for (i = 0; i < list->length; i++) {
        if (has_quit_code(list->values[i])) {
            return true;
        }
    }
    return false;
}

/**
 * Vyhodnoti predany AST, vysledek vypise do konzole.
 */
static Value * eval(SExpression *exp) {
    Value *v = eval_statement(exp);

    if (has_quit_code(v))
        return v;  /* zadny vypis, hned ukoncit */

    if (DEBUG) {
        printf("\n");
        printf("RESULT:\n");
        printf(" | ");
    }
    print_value(v);
    printf("\n");

    return v;
}

/**
 * Zpracuje vstup a vrati seznam vysledku (pro kazdy vyraz).
 * Vysledky vypisuje do konzole.
 * Volitelne muze zpracovat pouze prvni vyraz.
 */
static ValueList * process(char *input, boolean only_first) {
    SExpression *exp;
    ValueList *vals = NULL;

    exp = build_s_expressions(input);

    if (exp == NULL) {
        /* syntakticka chyba */
        if (reader_has_error()) {
            printf("Error: "); print_reader_error(); printf("\n");
        } else {
            assert(false);
        }
        vals = NULL;

    } else if (exp->length == 0) {
        /* na vstupu byly jen bile znaky */
        vals = valuelist_new(0, NULL, false);

    } else {
        /* AST v poradku vytvoren */
        if (DEBUG) print_expr(exp, 1);

        if (only_first) {
            Value *array[1];

            array[0] = eval(((SExpression **) exp->pointer)[0]);
            vals = valuelist_new(1, array, false);

        } else {
            Value **array;
            int i;

            /* alokace pameti */
            array = get_memory(exp->length, sizeof(Value *));

            /* vyhodnoceni vsech vyrazu */
            for (i = 0; i < exp->length; i++) {
                Value *v = eval(((SExpression **) exp->pointer)[i]);
                array[i] = v;
            }

            vals = valuelist_new(exp->length, array, false);
            free_memory(array);
        }
    }

    free_sexpr(exp);

    return vals;
}

int repl_start() {
    int return_code;
    char buff[REPL_MAX_INPUT_SIZE];

    while (1) {
        ValueList *list = NULL;

        print_new_input();
        fflush(stdout);
        return_code = console_read_line(buff, REPL_MAX_INPUT_SIZE);

        if (return_code == CONSOLE_NO_INPUT) {
            printf("\nNo input\n");
            continue;
        }

        if (return_code == CONSOLE_TOO_LONG) {
            printf("Input too long\n");
            continue;
        }

        if (buff[0] == 0) {
            /* nezadan zadny vstup */
            continue;
        }

        list = process(buff, false);
        if (contains_quit_code(list)) {
            free_valuelist(list);
            break;
        }

        free_valuelist(list);

        if (DEBUG) {
            printf("\n");
            printf("Allocated objects:\n");
            printf(" | %d\n", get_allocated_objects());
        }
    }
    return return_code;
}

/**
 * Vyhodnoti a vrati pouze prvni vyraz - to se hodi hlavne pro testovani.
 */
static Value *repl_eval_first(char *input) {
    ValueList *list;

    print_new_input();
    printf("%s\n", input);
    list = process(input, true);

    if (list == NULL)
        return value_exception("syntax error");
    else if (list->length == 0)
        return value_exception("no input");
    else
        return list->values[0];
}

int repl_eval_as_int(char *input, int error_code) {
    Value *v;
    int i;

    v = repl_eval_first(input);

    if (v->type != TYPE_INTEGER)
        return error_code;

    i = as_integer(v);
    free_value(v);

    return i;
}

int repl_eval_as_logical(char *input, int error_code) {
    Value *v;
    int i;

    v = repl_eval_first(input);

    if (v->type != TYPE_LOGICAL)
        return error_code;

    i = as_logical(v);
    free_value(v);

    return i;
}

/* --- VSTUP ZE SOUBORU --- */

#define MAX_STATEMENT_SIZE 2048

static FILE *file;

static int file_get_char() {
    return fgetc(file);
}

static void file_unget_char(int c) {
    ungetc(c, file);
}

/*
 * Rozseka vstpni soubor na jednotlive vyrazy a ty pole necha vyhodnotit
 * interpretrem. Interpreter by si sice dokazal poradit i s celym textem
 * najednou, ale uz bychom z nej nedostali jednotlive vstupni retezce,
 * ktere potrebujeme jednotlive vypsat do konzole.
 */
int repl_eval_file(char *file_name) {
    char buff[MAX_STATEMENT_SIZE];
    int size;

    file = fopen(file_name, "r");

    if (file == NULL) {
        printf("Cannot open the file");
        return EXIT_FAILURE;  /* problem s otevrenim souboru */
    }

    while ((size = next_expression(file_get_char, file_unget_char, MAX_STATEMENT_SIZE, buff)) > 0) {
        ValueList *list;

        print_new_input(); printf("%s\n", buff);

        list = process(buff, true);
        if (contains_quit_code(list)) {
            /* predcasne ukonceni */
            free_valuelist(list);
            break;
        }

        free_valuelist(list);
    }

    fclose(file);
    file = NULL;

    return EXIT_SUCCESS;
}
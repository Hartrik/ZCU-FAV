/*
 * File:   interpreter.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-06
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <ctype.h>

#include "commons.h"
#include "types.h"
#include "heap.h"
#include "interpreter.h"
#include "built-in.h"


/**
 * Vyhodnoti funkci.
 */
static Value * eval_function(int count, SExpression **content, Function function) {
    int argc = 0;
    Value **argv, *result;

    assert(count >= 0);

    /* alokace pameti pro vyhodnocene parametry */
    argv = get_memory(count, sizeof(Value *));

    if (count > 0) {
        int i;
        for (i = 0; i < count; i++) {
            Value *v = eval_statement(content[i]);
            if (v->type == TYPE_EXCEPTION) {
                /* pri vyhodnocovani parametru doslo k chybe */
                /* vyjimka se bude propagovat dale */

                /* recyklace jiz vyhodnocenych parametru */
                free_values(argc, argv);
                return v;
            }
            argv[argc++] = v;
        }
    }

    /* vyhodnoceni funkce */
    result = function(argc, argv);

    free_values(argc, argv);
    return result;
}

/**
 * Vyhodnoti specialni formu.
 */
static Value * eval_special_form(int count, SExpression **content, SpecialForm form) {
    assert(count >= 0);
    return form(count, content);
}

/**
 * Vyhodnoti slozenou formu (funkci nebo specialni formu).
 */
static Value * eval_compound_form(int count, SExpression **content) {
    Value *v = NULL;  /* hodnota s funkci */
    Value *result = NULL;

    assert(count >= 0);

    if (count == 0)
        return NIL;  /* prazdne zavorky */

    v = eval_statement(content[0]);

    if (v->type == TYPE_FUNCTION) {
        /* budeme vyhodnocovat funkci */
        result = eval_function(count - 1, content + 1, as_function(v));
    } else if (v->type == TYPE_SPECIAL_FORM) {
        /* budeme vyhodnocovat specialni formu */
        result = eval_special_form(count - 1, content + 1, as_special_form(v));
    } else if (v->type == TYPE_EXCEPTION) {
        /* propagace vyjimky */
        return v;
    } else {
        /* na prvnim miste v seznamu je neco jineho */
        result = value_exception(MSG_NOT_A_FUNCTION);
    }

    free_value(v);
    return result;
}

/**
 * Vyhodnoti identifikator.
 */
static Value * eval_identifier(char *identifier) {
    Value *v = heap_get(identifier);

    return (v) ? v : value_exception(MSG_UNKNOWN_IDENTIFIER);
}

/**
 * Testuje, zda je token cislo.
 */
static boolean test_is_number(char *token) {
    /* token ma vzdy alespon jeden znak */
    assert(strlen(token) >= 1);

    /* podle prvniho pismene (nebo prvnich dvou) rozpozna zda jde o cislo
       nebo identifikator */

    if (isdigit(token[0])) {
        return true;
    }

    if (strlen(token) > 1) {
        /* jeste to muze byt cislo se znamenkem */

        if ((token[0] == '+' || token[0] == '-') && isdigit(token[1])) {
            /* plus/minus nasledovane cislici => jedná se o cislo */
            return true;
        }
    }

    return false;
}

/**
 * Vyhodnoti token (literal nebo identifikator).
 */
static Value * eval_token(char *first_token) {
    if (test_is_number(first_token)) {
        /* jedna se o cislo */

        char *rest;
        int number = strtol(first_token, &rest, 10);

        if (strlen(rest) == 0)
            return value_integer(number);
        else
            return value_exception(MSG_WRONG_NUMBER_FORMAT);

    } else {
        /* nazev promenne nebo funkce */
        return eval_identifier(first_token);
    }
}

Value * eval_statement(SExpression *exp) {
    assert(exp != NULL);

    switch (exp->type) {
        case S_TOKEN:
            return eval_token((char *) exp->pointer);
        case S_LIST:
            return eval_compound_form(exp->length, (SExpression **) exp->pointer);
        default:
            assert(false);
    }
}

void interpreter_init() {
    heap_init();
    builtin_init();
}

void interpreter_shutdown() {
    heap_free();
}
/*
 * File:   reader.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-14
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include "commons.h"
#include "types.h"
#include "lexer.h"
#include "reader.h"


/* CHYBY */

static int reader_error_code = 0;

boolean reader_has_error() {
    return (reader_error_code != 0) ? true : false;
}

void print_reader_error() {
    switch (reader_error_code) {
        case ERROR_L_PAR_EXPECTED: printf(ERROR_L_PAR_EXPECTED_MSG); return;
        case ERROR_R_PAR_EXPECTED: printf(ERROR_R_PAR_EXPECTED_MSG); return;
        case ERROR_QUOTE_ALONE: printf(ERROR_QUOTE_ALONE_MSG); return;
        default: printf(ERROR_UNKNOWN_MSG);
    }
}

/* PARSOVANI - vytvareni AST */

/** Vytvori s-vyraz z tokenu. */
static SExpression * parse_token(int count, char **tokens) {
    return sexpr_new(S_TOKEN, 1, copy_string(tokens[0]));
}

/** Vytvori s-vyraz ze seznamu. */
static SExpression * parse_list(int count, char **tokens) {
    SExpression **buff = get_memory(S_EXPRESSION_DEF_SIZE, sizeof(SExpression *));
    int buff_size = S_EXPRESSION_DEF_SIZE;
    int buff_index = 0;

    SExpression *exp = NULL;
    boolean new_exp = false;

    int level = 1;  /* uz jsme uvnitr prvni zavorky */
    int inner_start;  /* index tokenu, kde zacina vnoreny seznam */
    int i;

    if (count == 1) {
        reader_error_code = ERROR_R_PAR_EXPECTED;
        return NULL;
    }

    for (i = 1; i < count; i++) {
        char *token = tokens[i];

        switch (token[0]) {
            case '(':
                if (level++ == 1) {
                    /* zde zacina vnorena slozena forma */
                    inner_start = i;
                }
                break;
            case ')':
                if (--level == 1) {
                    /* zde konci vnorena slozena forma */
                    exp = parse_list(i - inner_start + 1, tokens + inner_start);
                    new_exp = true;
                }
                break;
            default:
                if (level == 1) {
                    /* vnoreny vyraz */
                    new_exp = true;
                    exp = parse(1, tokens + i);
                }
        }

        if (exp != NULL && new_exp) {
            /* vyhodnocen dalsi parametr */

            /* ulozeni hodnoty do pole */
            if (buff_index + 1 >= buff_size)
                buff = realloc(buff, (buff_size *= S_EXPRESSION_INC_RATIO) * sizeof(char *));

            buff[buff_index++] = exp;

            exp = NULL;
            new_exp = false;

        } else {
            if (new_exp) {
                /* propagace chyby */
                free_sexprs(buff_index, buff);
                return NULL;
            }
        }

        if (level == 0) {
            /* nasli jsme top-level ukoncujici zavorku */
            break;
        }
    }

    if ((level == 0) && (i < count - 1)) {
        /* nezpracovali jsme vsechny tokeny */
        free_sexprs(buff_index, buff);
        reader_error_code = ERROR_L_PAR_EXPECTED;
        return NULL;
    }
    if (level != 0) {
        /* slozena forma nebyla spravne ukoncena */
        free_sexprs(buff_index, buff);
        reader_error_code = ERROR_R_PAR_EXPECTED;
        return NULL;
    }

    return sexpr_new(S_LIST, buff_index, buff);
}

SExpression * parse(int count, char **tokens) {
    char first_char, *first_token;

    if (count == 0) {
        /* na vstupu jsou jen bile znaky */
        return NULL;
    }

    first_token = tokens[0];
    first_char = first_token[0];

    switch (first_char) {
        case '(':
            /* slozena forma */
            return parse_list(count, tokens);
        case ')':
            /* neco je spatne */
            reader_error_code = ERROR_L_PAR_EXPECTED;
            return NULL;
        default:
            /* musi se jednat o hodnotu nebo literal */
            return parse_token(count, tokens);
    }
}

/* MAKRA */

/**
 * Urci, zda se jedna o vyraz obsahujici '.
 */
static boolean is_quote_token(SExpression *exp) {
    return (exp->type == S_TOKEN)
            && strcmp((char *) exp->pointer, QUOTE_CHAR_STR) == 0;
}

/**
 * Najde prvni vyraz obsahujici ' a vrati jeho pozici.
 * Jinak vrati zapornou hodnotu.
 */
static int find_quote(int count, SExpression **array) {
    int i;
    for (i = 0; i < count; i++) {
        if (is_quote_token(array[i])) {
            int next = i + 1;
            /* '''A => ''(QUOTE A) */
            if ((next < count) && !is_quote_token(array[next])) {
                return i;
            }
        }
    }
    return -1;
}

/**
 * Transformuje prvni nalezeny s-vyraz: '<form> => (QUOTE <form>).
 * Po provedeni transformace vrati true, jinak false.
 */
static boolean apply_quote_macro_first(SExpression * exp) {
    SExpression **array = (SExpression **) exp->pointer;
    int i, quote_index = find_quote(exp->length, array);

    if (quote_index >= 0) {
        /* nahrazeni '<form> za (QUOTE <form>) */
        /* vyuzijeme stavajici struktury a pole znovu vytvorime */
        SExpression **new_array = get_memory(exp->length - 1, sizeof(SExpression *));

        SExpression *quote = array[quote_index];
        SExpression *form = array[quote_index + 1];
        char *quote_str = (char *) quote->pointer;

        /* pole pro "QUOTE" a "<form>" */
        SExpression **quote_array = get_memory(2, sizeof(SExpression *));
        quote_array[0] = sexpr_new(S_TOKEN, 1, copy_string(QUOTE_FUNCTION));
        quote_array[1] = form;

        quote->type = S_LIST;
        quote->length = 2;  /* obsahuje "QUOTE" a "<form>" */
        quote->pointer = quote_array;

        /* kopirovani pole */
        for (i = 0; i < exp->length; i++) {
            if (i <= quote_index) {
                new_array[i] = array[i];
            } else if (i > quote_index + 1 && i < exp->length) {
                new_array[i - 1] = array[i];
            }
        }

        free_memory(quote_str);   /* uvolneni retezce ' */
        free_memory(array);       /* uvolneni stareho pole */

        exp->length--;
        exp->pointer = new_array;
        return true;
    }
    return false;
}

/**
 * Transformuje AST: '<form> => (QUOTE <form>).
 */
static void apply_quote_macro(SExpression * exp) {
    if (exp->type == S_LIST) {
        int i;

        while (apply_quote_macro_first(exp))
            ; /* maker muze byt na jedne radce nekolik a klidne i za sebou */

        /* aplikace makra na vnorene seznamy */
        for (i = 0; i < exp->length; i++) {
            apply_quote_macro(((SExpression **) exp->pointer)[i]);
        }
    }
}

/** Aplikuje na AST makra. */
static void apply_reader_macros(SExpression * exp) {
    apply_quote_macro(exp);
}

/* OSTATNI */

static char *input_buffer = NULL;
static int input_buffer_index = -1;

/** Vrati dalsi znak z bufferu nebo \0. */
static char next_char() {
    char c = input_buffer[input_buffer_index++];

    if (c == '\0') {
        input_buffer_index--; /* pri dalsim zavolani znovu vrati \0 */
    }

    return c;
}

/** Vrati posledni znak zpet do bufferu. */
static void return_char() {
    input_buffer_index--;
}

/** Rozdeli obsah bufferu na tokeny a vysledek nastavi do predanych parametru. */
static void collect_tokens(int *count, char ***tokens) {
    char **buffer = get_memory(BUFFER_DEF_SIZE, sizeof(char *));
    int b_size = BUFFER_DEF_SIZE;
    int b_index = 0;

    char *token;

    /* ulozeni tokenu do bufferu */
    /*   jsou pridany obalovaci zavorky protoze ve vstupnim retezci */
    /*   muze byt vice prikazu; root bude vzdy list */

    buffer[b_index++] = copy_string("(");

    while ((token = next_token(next_char, return_char)) != NULL) {
        if (b_index + 1 >= b_size)
            buffer = realloc(buffer, (b_size *= BUFFER_INC_RATIO) * sizeof(char *));

        buffer[b_index++] = token;
    }

    if (b_index + 1 >= b_size)
        buffer = realloc(buffer, (b_size += 1) * sizeof(char *));

    buffer[b_index++] = copy_string(")");

    *count = b_index;
    *tokens = buffer;
}

SExpression * build_s_expressions(char *input) {
    SExpression *result;

    char **tokens;
    int count;

    /* nastaveni globalnich promennych */
    /* bez nich to bohuzel nejde */
    reader_error_code = 0;
    input_buffer = input;
    input_buffer_index = 0;

    collect_tokens(&count, &tokens);
    result = parse(count, tokens);

    /* aplikace maker - pouze pokud nedoslo k syntakticke chybe */
    if (result != NULL)
        apply_reader_macros(result);

    /* uvolneni pameti tokenu */
    free_strings(count, tokens);
    input_buffer = NULL;  /* uvolneni vstupu neni nase starost */
    return result;
}

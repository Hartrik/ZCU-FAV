/*
 * File:   lexer.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-03
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <assert.h>

#include "commons.h"
#include "lexer.h"


/* --- TOKENIZACE --- */

static char token_buff[MAX_TOKEN_SIZE];

/**
 * Alokuje novy token, pokud neni buffer prazdny.
 */
static char * create_token(char *buffer, int length) {
    if (length > 0) {
        char *token = (char *) get_memory((length + 1 /* \0 */), sizeof(char));
        strncpy(token, buffer, length);
        return token;

    } else {
        return NULL;
    }
}

char * next_token(char (*next_char)(), void (*return_char)()) {
    int token_buff_index = 0;
    char next;

    while ((next = next_char()) > 0 /* EOF & \0 */) {
        next = toupper(next);

        switch (next) {
            /* samostatne tokeny */
            case '(':
            case ')':
            case '\'':
                /* ukonceni tokenu */
                if (token_buff_index > 0) {
                    /* vraceni znaku - tento znak bude az v dalsim tokenu */
                    return_char();

                    /* ukonceni tokenu */
                    return create_token(token_buff, token_buff_index);

                } else {
                    /* buffer je prazdny */
                    token_buff[token_buff_index++] = next;
                    return create_token(token_buff, token_buff_index);
                }

            /* whitespace */
            case ' ':
            case '\t':
            case '\n':
            case '\r':
                /* ukonceni tokenu */
                if (token_buff_index > 0)
                    return create_token(token_buff, token_buff_index);

                break;

            /* non-whitespace znaky */
            default:
                /* delsi tokeny se proste useknou
                 * (ale to by se bude dit jen v extremnich pripadech) */
                if (token_buff_index < MAX_TOKEN_SIZE)
                    token_buff[token_buff_index++] = next;
        }
    }

    return create_token(token_buff, token_buff_index);
}


/* --- ROZDELENI VYRAZU (nacitani ze souboru) --- */

int next_expression(int (*next_char)(), void (*return_char)(int),
        int buff_size, char *buff) {

    int buff_index = 0;
    int next;
    int level = 0;
    boolean text = false;

    while ((next = next_char()) > 0 /* EOF & \0 */) {
        char next_char = (char) next;
        boolean end = false;

        switch (next_char) {
            case '(':
                if (text) {
                    return_char(next);
                    end = true;  /* konec vyrazu */
                } else {
                    level++;
                    if (buff_index < buff_size)
                        buff[buff_index++] = next_char;
                }
                break;

            case ')':
                if (buff_index < buff_size)
                    buff[buff_index++] = next_char;
                if (--level == 0)
                    end = true;  /* konec slozene formy */
                break;

            case ' ':
            case '\t':
            case '\n':
            case '\r':
                if (level > 0) {
                    if (buff_index < buff_size)
                        buff[buff_index++] = next_char;
                } else if (text) {
                    end = true;  /* konec vyrazu */
                } else {
                    /* jenom whitespace na zacatku vyrazu... */
                }
                break;

            case '\'':
                if (text) {
                    return_char(next);
                    end = true;  /* konec vyrazu */
                } else {
                    if (buff_index < buff_size)
                        buff[buff_index++] = next_char; /* bez text = true */
                }
                break;

            default:
                if (level == 0) text = true;
                if (buff_index < buff_size)
                    buff[buff_index++] = next_char;
        }

        if (end && buff_index > 0) {
            break;
        }
    }

    /* vyhodnoceni posledniho vyrazu */
    if (buff_index > 0) {
        if (buff_index < buff_size)
                buff[buff_index] = '\0';
            else
                buff[--buff_index] = '\0';

        return buff_index;
    }

    return -1;
}
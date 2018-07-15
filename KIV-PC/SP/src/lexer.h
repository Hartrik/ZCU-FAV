/*
 * File:   lexer.h
 * Author: Patrik Harag
 *
 * Created: 2016-11-03
 */

#ifndef LEXER_H
#define	LEXER_H

#define MAX_TOKEN_SIZE 256

/**
 * Vrati dalsi token. Token ma delku minimalne 1.
 *
 * @param next_char funkce vracejici dalsi znak
 * @param return_char funkce pro vraceni znaku
 * @return token
 */
char * next_token(char (*next_char)(), void (*return_char)());

/**
 * Ulozi do bufferu dalsi vyraz a vrati jeho delku.
 *
 * @param next_char funkce vracejici dalsi znak
 * @param return_char funkce pro vraceni znaku
 * @param buffer_size velikost bufferu
 * @param buffer buffer
 * @return delka ulozeneho retezce,
 *         zaporna delka pokud na vstupu nebyl zadny vyraz
 */
int next_expression(int (*next_char)(), void (*return_char)(int),
        int buffer_size, char *buffer);

#endif	/* LEXER_H */

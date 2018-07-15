/*
 * File:   repl.h
 * Author: Patrik Harag
 *
 * Created: 2016-11-06
 */

#ifndef REPL_H
#define	REPL_H

#define REPL_MAX_INPUT_SIZE 512

#define REPL_QUIT_CODE "<quit>"

/**
 * Spusti read–eval–print loop.
 *
 * @return exit code
 */
int repl_start();

/**
 * Vypise vyraz na standardni vystup, vyhodnoti, vysledek vypise a vrati.
 *
 * @param input vyraz v lispu
 * @param error navratova hodnota v pripade chyby
 * @return vysledek vyhodnoceni
 */
int repl_eval_as_int(char *input, int error);

/**
 * Vypise vyraz na standardni vystup, vyhodnoti, vysledek vypise a vrati.
 *
 * @param input vyraz v lispu
 * @param error navratova hodnota v pripade chyby
 * @return vysledek vyhodnoceni
 */
int repl_eval_as_logical(char *input, int error);

/**
 * Vyhodnoti kod ulozeny v souboru.
 * Kazdy nacteny vyraz vypise do konzole, vyhodnoti a vysledek vypise.
 *
 * @return exit code
 */
int repl_eval_file(char *file_name);

#endif	/* REPL_H */
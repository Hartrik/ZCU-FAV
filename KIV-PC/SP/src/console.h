/*
 * Obsahuje funkce pro praci s konzoli.
 *
 * File:   console.h
 * Author: Patrik Harag
 *
 * Created: 2016-11-03
 */

#ifndef CONSOLE_H
#define	CONSOLE_H

#define CONSOLE_OK       0
#define CONSOLE_NO_INPUT 1
#define CONSOLE_TOO_LONG 2

/**
 * Nacte z konzole dalsi radek.
 *
 * @param buffer
 * @param buffer_size
 * @return uspesnost operace
 */
int console_read_line(char *buffer, int buffer_size);

#define FORMAT_NEW_INPUT "[%d]> "
#define FORMAT_S_EXPRESSION_INDENT "    "

/**
 * Vypise retezec pred zacatkem dalsiho vstupu.
 */
void print_new_input();

/**
 * Vypise s-vyraz.
 *
 * @param exp s-vyraz
 * @param indent pocatecni odsazeni
 */
void print_expr(SExpression *exp, int indent);

/**
 * Vypise hodnotu.
 *
 * @param v hodnota
 */
void print_value(Value *v);

#endif	/* CONSOLE_H */
/*
 * Soubor definujici vestavene systemove funkce jazyka.
 *
 * File:   built-in_system.h
 * Author: Patrik Harag
 *
 * Created: 2016-11-13
 */

#ifndef BUILT_IN_SYSTEM_H
#define	BUILT_IN_SYSTEM_H

#define MSG_CANNOT_CHANGE_CONSTANT "cannot change constant"
#define MSG_NOT_LOGICAL "given expression is not of a logical type"

/* SPECIALNI FORMY */

/** Built-in specialni forma: quote. */
Value * form_quote(int count, SExpression **exprs);

/** Built-in specialni forma: prikaz if. */
Value * form_if(int count, SExpression **exprs);

/* OSTATNI FUNKCE */

/** Built-in funkce: ukonceni programu. */
Value * func_quit(int argc, Value **argv);

/** Built-in funkce: nastaveni promenne. */
Value * func_set(int argc, Value **argv);

#endif	/* BUILT_IN_SYSTEM_H */
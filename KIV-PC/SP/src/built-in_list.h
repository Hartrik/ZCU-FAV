/*
 * Soubor definujici vestavene funkce pro praci se seznamy.
 *
 * File:   built-in_list.h
 * Author: Patrik Harag
 *
 * Created: 2016-11-13
 */

#ifndef BUILT_IN_LIST_H
#define	BUILT_IN_LIST_H

/** Built-in funkce: vytvori z parametru novy seznam. */
Value * func_list(int argc, Value **argv);

/** Built-in funkce: vrati delku seznamu. */
Value * func_list_length(int argc, Value **argv);

/** Built-in funkce: vrati prvek na danem indexu. */
Value * func_list_elt(int argc, Value **argv);

/** Built-in funkce: vrati prvni prvek. */
Value * func_list_car(int argc, Value **argv);

/** Built-in funkce: vrati seznam bez prvniho prvku. */
Value * func_list_cdr(int argc, Value **argv);

/** Built-in funkce: pripoji prvek na zacatek seznamu. */
Value * func_list_cons(int argc, Value **argv);

/** Built-in funkce: pripoji prvek na konec seznamu. */
Value * func_list_conj(int argc, Value **argv);

/** Built-in funkce: redukuje seznam na jedinou hodnotu */
Value * func_list_reduce(int argc, Value **argv);

#endif	/* BUILT_IN_LIST_H */

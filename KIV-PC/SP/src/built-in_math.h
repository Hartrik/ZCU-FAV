/*
 * Soubor definujici vestavene matematicke funkce jazyka.
 *
 * File:   built-in_math.h
 * Author: Patrik Harag
 *
 * Created: 2016-11-13
 */

#ifndef BUILT_IN_MATH_H
#define	BUILT_IN_MATH_H

#define MSG_DIVISION_BY_ZERO "division by zero"

/* ARITMETICKE FUNKCE */

/** Built-in funkce: secte prvky. */
Value * func_addition(int argc, Value **argv);

/** Built-in funkce: odecte prvky. */
Value * func_subtraction(int argc, Value **argv);

/** Built-in funkce: vynasobi prvky. */
Value * func_multiplication(int argc, Value **argv);

/** Built-in funkce: vydeli prvky. */
Value * func_division(int argc, Value **argv);

/* POROVNAVACI FUNKCE (operatory) */

/** Built-in funkce: porovna prvky operatorem =. */
Value * func_equals(int argc, Value **argv);

/** Built-in funkce: porovna prvky operatorem !=. */
Value * func_not_equals(int argc, Value **argv);

/** Built-in funkce: porovna prvky operatorem <. */
Value * func_less_than(int argc, Value **argv);

/** Built-in funkce: porovna prvky operatorem >. */
Value * func_greater_than(int argc, Value **argv);

/** Built-in funkce: porovna prvky operatorem <=. */
Value * func_less_than_or_equals(int argc, Value **argv);

/** Built-in funkce: porovna prvky operatorem >=. */
Value * func_greater_than_or_equals(int argc, Value **argv);

#endif	/* BUILT_IN_MATH_H */
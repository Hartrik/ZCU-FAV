/*
 * File:   built-in.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-09
 */

#include <stdio.h>
#include <stdlib.h>

#include "commons.h"
#include "types.h"
#include "heap.h"
#include "built-in.h"

#include "built-in_math.h"
#include "built-in_system.h"
#include "built-in_list.h"


void builtin_init() {
    /* zakladni konstanty */
    heap_set("T", T, true);
    heap_set("NIL", NIL, true);

    /* aritmeticke funkce */
    heap_set("+", value_function(func_addition), false);
    heap_set("-", value_function(func_subtraction), false);
    heap_set("*", value_function(func_multiplication), false);
    heap_set("/", value_function(func_division), false);

    /* porovnavaci funkce */
    heap_set("=", value_function(func_equals), false);
    heap_set("/=", value_function(func_not_equals), false);
    heap_set("<", value_function(func_less_than), false);
    heap_set(">", value_function(func_greater_than), false);
    heap_set("<=", value_function(func_less_than_or_equals), false);
    heap_set(">=", value_function(func_greater_than_or_equals), false);

    /* funkce pro praci se seznamem */
    heap_set("LIST", value_function(func_list), false);
    heap_set("LENGTH", value_function(func_list_length), false);
    heap_set("ELT", value_function(func_list_elt), false);
    heap_set("CAR", value_function(func_list_car), false);
    heap_set("CDR", value_function(func_list_cdr), false);
    heap_set("CONS", value_function(func_list_cons), false);
    heap_set("CONJ", value_function(func_list_conj), false);
    heap_set("REDUCE", value_function(func_list_reduce), false);

    /* systemove funkce */
    heap_set("IF", value_special_form(form_if), true);
    heap_set("SET", value_function(func_set), true);
    heap_set("QUOTE", value_special_form(form_quote), true);
    heap_set("QUIT", value_function(func_quit), true);
}
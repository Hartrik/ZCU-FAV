/*
 * File:   built-in_list.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-13
 */

#include <stdio.h>
#include <stdlib.h>

#include "commons.h"
#include "types.h"
#include "built-in.h"
#include "built-in_list.h"


/* FUNKCE PRO PRACI SE SEZNAMY */

Value * func_list(int argc, Value **argv) {
    return value_list(argc, argv);
}

Value * func_list_length(int argc, Value **argv) {
    if (argc < 1)
        return value_exception(MSG_TOO_FEW_ARGUMENTS);

    if (argv[0]->type != TYPE_LIST)
        return value_exception(MSG_WRONG_TYPE);

    return value_integer(as_list(argv[0])->length);
}

Value * func_list_elt(int argc, Value **argv) {
    Value *v_list = NULL, *v_index = NULL;
    ValueList *list;
    int index;

    if (argc < 2)
        return value_exception(MSG_TOO_FEW_ARGUMENTS);

    v_list = argv[0];
    v_index = argv[1];

    if (v_list->type != TYPE_LIST || v_index->type != TYPE_INTEGER)
        return value_exception(MSG_WRONG_TYPE);

    list = as_list(v_list);
    index = as_integer(v_index);

    return (index >= 0 && index < list->length)
            ? copy_value(list->values[index])
            : value_exception(MSG_OUT_OF_BOUNDS);
}

Value * func_list_car(int argc, Value **argv) {
    Value *v = NULL;

    if (argc < 1)
        return value_exception(MSG_TOO_FEW_ARGUMENTS);

    v = argv[0];

    if (v->type != TYPE_LIST)
        return value_exception(MSG_WRONG_TYPE);

    return (as_list(v)->length != 0)
            ? copy_value(as_list(v)->values[0])
            : NIL;
}

Value * func_list_cdr(int argc, Value **argv) {
    Value *v = NULL;

    if (argc < 1)
        return value_exception(MSG_TOO_FEW_ARGUMENTS);

    v = argv[0];

    if (v->type != TYPE_LIST)
        return value_exception(MSG_WRONG_TYPE);

    return (as_list(v)->length != 0)
            ? value_list(as_list(v)->length - 1, as_list(v)->values + 1)
            : NIL;
}

/** Vyctovy typ definujici misto, kam se pripoji novy element. */
typedef enum { start, end } Position;

/** Pripoji novy element na danou pozici. */
static Value * list_append_value(int argc, Value **argv, Position p) {
    Value *value_1 = NULL, *value_2 = NULL;
    ValueList *list = NULL;

    if (argc < 2)
        return value_exception(MSG_TOO_FEW_ARGUMENTS);

    value_1 = argv[0];
    value_2 = argv[1];

    if (value_2->type != TYPE_LIST)
        return value_exception(MSG_WRONG_TYPE);

    list = as_list(value_2);

    if (list->length == 0) {
        Value *values[1]; values[0] = value_1;
        return value_list(1, values);

    } else {
        Value **values, *result;
        int i;

        /* alokace pameti */
        values = get_memory(list->length + 1, sizeof(Value *));

        /* vlozeni nove hodnoty do pole */
        values[p == start ? 0 : list->length] = value_1;

        /* kopirovani ostatnich hodnot */
        for (i = 0; i < list->length; i++) {
            values[i + (p == start ? 1 : 0)] = list->values[i];
        }

        result = value_list(list->length + 1, values);
        free_memory(values);
        return result;
    }
}

Value * func_list_cons(int argc, Value **argv) {
    return list_append_value(argc, argv, start);
}

Value * func_list_conj(int argc, Value **argv) {
    return list_append_value(argc, argv, end);
}

/** Redukuje seznam na jedinou hodnotu.  */
static Value * reduce(Value *v_func, Value *v_def, Value *v_list) {
    Function f = as_function(v_func);
    Value *acc = v_def;
    ValueList *list = as_list(v_list);
    int i = 0;

    for (; i < list->length; i++) {
        Value *argv[2];
        argv[0] = acc;
        argv[1] = list->values[i];

        acc = f(2, argv);

        if (i > 0) {
            /* uvolneni mezivysledku */
            free_value(argv[0]);
        }

        if (acc->type == TYPE_EXCEPTION)
            break;
    }

    if (i > 0) {
        return acc;
    } else {
        /* nemuzeme vratit primo parametr */
        return copy_value(acc);
    }
}

Value * func_list_reduce(int argc, Value **argv) {
    if (argc < 3)
        return value_exception(MSG_TOO_FEW_ARGUMENTS);

    if (argc > 3)
        return value_exception(MSG_TOO_MANY_ARGUMENTS);

    if (argv[0]->type != TYPE_FUNCTION)
        return value_exception(MSG_WRONG_TYPE);

    if (argv[2]->type != TYPE_LIST)
        return value_exception(MSG_WRONG_TYPE);

    return reduce(argv[0], argv[1], argv[2]);
}
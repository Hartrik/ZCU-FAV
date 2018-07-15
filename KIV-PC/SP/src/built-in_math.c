/*
 * File:   built-in_math.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-13
 */

#include <stdio.h>
#include <stdlib.h>

#include "commons.h"
#include "types.h"
#include "built-in.h"
#include "built-in_math.h"


/* ARITMETICKE FUNKCE */

/** Retezove aplikuje danou funkci na seznam hodnot. */
Value * reduce_int(int argc, Value **argv, int def, int (*func) (int, int)) {
    int i, result = def;

    for (i = 0; i < argc; i++) {
        Value *v = argv[i];

        if (v->type == TYPE_INTEGER) {
            result = func(result, as_integer(v));
        } else {
            return value_exception(MSG_WRONG_TYPE);
        }
    }

    return value_integer(result);
}

/** Retezove aplikuje danou funkci na seznam hodnot. */
static Value * reduce_int_first(int argc, Value **argv, int (*func) (int, int)) {
    if (argc < 2) {
        return value_exception(MSG_TOO_FEW_ARGUMENTS);

    } else {
        Value *v = argv[0];
        if (v -> type != TYPE_INTEGER)
            return value_exception(MSG_WRONG_TYPE);

        return reduce_int(argc - 1, argv + 1, as_integer(v), func);
    }
}

static int lambda_plus(int a, int b) {
    return a + b;
}

static int lambda_minus(int a, int b) {
    return a - b;
}

static int lambda_multiply(int a, int b) {
    return a * b;
}

Value * func_addition(int argc, Value **argv) {
    return reduce_int(argc, argv, 0, lambda_plus);
}

Value * func_subtraction(int argc, Value **argv) {
    if (argc < 1) {
        return value_exception(MSG_TOO_FEW_ARGUMENTS);

    } else if (argc == 1) {
        Value *v = argv[0];
        if (v -> type != TYPE_INTEGER)
            return value_exception(MSG_WRONG_TYPE);

        return value_integer(-as_integer(v));

    } else {
        return reduce_int_first(argc, argv, lambda_minus);
    }
}

Value * func_multiplication(int argc, Value **argv) {
    return reduce_int(argc, argv, 1, lambda_multiply);
}

Value * func_division(int argc, Value **argv) {
    if (argc < 2) {
        return value_exception(MSG_TOO_FEW_ARGUMENTS);

    } else {
        Value *v = argv[0];
        int i, result;

        if (v -> type != TYPE_INTEGER)
            return value_exception(MSG_WRONG_TYPE);

        result = as_integer(v);

        for (i = 1; i < argc; i++) {
            v = argv[i];

            if (v->type == TYPE_INTEGER) {
                int d = as_integer(v);
                if (d == 0)
                    return value_exception(MSG_DIVISION_BY_ZERO);

                result /= d;

            } else {
                return value_exception(MSG_WRONG_TYPE);
            }
        }

        return value_integer(result);
    }
}

/* POROVNAVACI FUNKCE (operatory) */

/** Porovna prvky zadanym operatorem. */
static Value * compare(int argc, Value **argv, int (*operator) (int, int)) {
    int i, last;

    for (i = 0; i < argc; i++) {
        Value *v = argv[i];

        if (v->type == TYPE_INTEGER) {
            int val = as_integer(v);

            if (i == 0 || operator(last, val)) {
                last = val;
            } else {
                return NIL;
            }
        } else {
            return value_exception(MSG_WRONG_TYPE);
        }
    }

    return T;
}

static int lambda_equal(int a, int b) {
    return a == b;
}

static int lambda_not_equal(int a, int b) {
    return a != b;
}

static int lambda_less_than(int a, int b) {
    return a < b;
}

static int lambda_greater_than(int a, int b) {
    return a > b;
}

static int lambda_less_than_or_equal(int a, int b) {
    return a <= b;
}

static int lambda_greater_than_or_equal(int a, int b) {
    return a >= b;
}

Value * func_equals(int argc, Value **argv) {
    return compare(argc, argv, lambda_equal);
}

Value * func_not_equals(int argc, Value **argv) {
    return compare(argc, argv, lambda_not_equal);
}

Value * func_less_than(int argc, Value **argv) {
    return compare(argc, argv, lambda_less_than);
}

Value * func_greater_than(int argc, Value **argv) {
    return compare(argc, argv, lambda_greater_than);
}

Value * func_less_than_or_equals(int argc, Value **argv) {
    return compare(argc, argv, lambda_less_than_or_equal);
}

Value * func_greater_than_or_equals(int argc, Value **argv) {
    return compare(argc, argv, lambda_greater_than_or_equal);
}
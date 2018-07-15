/*
 * File:   built-in_system.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-13
 */

#include <stdio.h>
#include <stdlib.h>

#include "commons.h"
#include "types.h"
#include "built-in.h"
#include "built-in_system.h"
#include "heap.h"
#include "interpreter.h"
#include "repl.h"


/* QUOTE */

/** Aplikuje funkci quote na token. */
static Value * quote_value(SExpression *exp) {
    return value_symbol((char *) exp->pointer);
}

/** Aplikuje funkci quote na seznam. */
static Value * quote_list(SExpression *exp) {
    SExpression **exps = (SExpression **) exp->pointer;
    Value **values;
    ValueList* list;
    int i;

    /* alokace pameti */
    values = get_memory(exp->length, sizeof(Value *));

    for (i = 0; i < exp->length; i++) {
        values[i] = (exps[i]->type == S_LIST)
                ? quote_list(exps[i])
                : quote_value(exps[i]);
    }

    list = valuelist_new(exp->length, values, false);
    free_memory(values);
    return value_new(TYPE_LIST, list);
}

Value * form_quote(int count, SExpression **exps) {
    if (count == 0) {
        return value_exception(MSG_TOO_FEW_ARGUMENTS);

    } else if (count > 1) {
        return value_exception(MSG_TOO_MANY_ARGUMENTS);

    } else {
        SExpression *exp = exps[0];
        return (exp->type == S_LIST) ? quote_list(exp) : quote_value(exp);
    }
}

/* IF */

Value * form_if(int count, SExpression **exprs) {
    if (count < 3) {
        return value_exception(MSG_TOO_FEW_ARGUMENTS);

    } else if (count > 3) {
        return value_exception(MSG_TOO_MANY_ARGUMENTS);

    } else {
        SExpression *form_cond = exprs[0];
        SExpression *form_true = exprs[1];
        SExpression *form_false = exprs[2];
        Value *cond;

        cond = eval_statement(form_cond);

        if (cond->type == TYPE_EXCEPTION) {
            /* propagace vyjimky */
            return cond;
        } else if (cond->type != TYPE_LOGICAL) {
            /* chyba - podminka neni logicka hodnota */
            free_value(cond);
            return value_exception(MSG_NOT_LOGICAL);
        } else {
            /* vyhodnoceni jedne z vetvi */
            Value *result = as_logical(cond)
                    ? eval_statement(form_true)
                    : eval_statement(form_false);

            free_value(cond);
            return result;
        }
    }
}

/* OSTATNI FUNKCE */

Value * func_quit(int argc, Value **argv) {
    return value_exception(REPL_QUIT_CODE);
}

Value * func_set(int argc, Value **argv) {
    Value *name = NULL, *value = NULL;
    boolean success;

    if (argc < 2)
        return value_exception(MSG_TOO_FEW_ARGUMENTS);

    name = argv[0];
    value = argv[1];

    if (name->type != TYPE_SYMBOL)
        return value_exception(MSG_WRONG_TYPE);

    success = heap_set(as_symbol(name), copy_value(value), false);
    return success ? copy_value(value) : value_exception(MSG_CANNOT_CHANGE_CONSTANT);
}
/*
 * File:   types.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-06
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include "commons.h"
#include "types.h"


/* s-expressions */

SExpression * sexpr_new(SType type, int length, void *pointer) {
    SExpression *exp = get_memory(1, sizeof(SExpression));
    exp->type = type;
    exp->length = length;
    exp->pointer = pointer;

    return exp;
}

void free_sexpr(SExpression *exp) {
    int i;

    if (exp == NULL) return;

    switch (exp->type) {
        case S_LIST:
            for (i = 0; i < exp->length; i++) {
                free_sexpr(((SExpression **) exp->pointer)[i]);
            }
        case S_TOKEN:
            free_memory(exp->pointer);
            free_memory(exp);
            break;
        default:
            assert(false);
    }
}

void free_sexprs(int count, SExpression ** array) {
    int i;

    for (i = 0; i < count; i++) {
        free_sexpr(array[i]);
    }
    free_memory(array);
}

/* vytvareni hodnot */

Value * value_new(int type, void *pointer) {
    Value *v = get_memory(1, sizeof(Value));
    v->type = type;
    v->pointer = pointer;

    return v;
}

ValueList * valuelist_new(int length, Value **values, boolean deep_copy) {
    ValueList *copy = get_memory(1, sizeof(ValueList));
    Value **copy_vals = NULL;

    if (length != 0) {
        int i;

        copy_vals = (Value **) get_memory(length, sizeof(Value **));

        /* zkopirovani vsech hodnot */
        for (i = 0; i < length; i++) {
            copy_vals[i] = deep_copy ? copy_value(values[i]) : values[i];
        }
    }

    copy->length = length;
    copy->values = copy_vals;

    return copy;
}

Value * value_logical_NIL() {
    int *i = get_memory(1, sizeof(int));
    *i = 0;

    /* logicke hodnoty by klidne mohly byt konstanty, to by ale zkomplikovalo
       naslednou spravu pameti; prozatim je tedy necham takto... */

    return value_new(TYPE_LOGICAL, i);
}

Value * value_logical_T() {
    int *i = get_memory(1, sizeof(int));
    *i = 1;  /* pro konzistenci */

    return value_new(TYPE_LOGICAL, i);
}

Value * value_symbol(char * symbol) {
    return value_new(TYPE_SYMBOL, copy_string(symbol));
}

Value * value_exception(char * message) {
    return value_new(TYPE_EXCEPTION, copy_string(message));
}

Value * value_integer(int integer) {
    int *i = get_memory(1, sizeof(int));
    *i = integer;

    return value_new(TYPE_INTEGER, i);
}

Value * value_list(int length, Value **values) {
    assert(length >= 0);
    return value_new(TYPE_LIST, valuelist_new(length, values, true));
}

Value * value_function(Function f) {
    FunctionWrapper *wrapper;

    assert(f != NULL);

    wrapper = get_memory(1, sizeof(FunctionWrapper));
    wrapper->function = f;
    return value_new(TYPE_FUNCTION, wrapper);
}

Value * value_special_form(SpecialForm f) {
    SpecialFormWrapper *wrapper;

    assert(f != NULL);

    wrapper = get_memory(1, sizeof(SpecialFormWrapper));
    wrapper->function = f;
    return value_new(TYPE_SPECIAL_FORM, wrapper);
}

/* ostatni funkce */

void free_value(Value *v) {
    if (v != NULL) {
        if (v->type == TYPE_LIST) {
            free_valuelist(as_list(v));
        } else {
            free_memory(v->pointer);
        }

        free_memory(v);
    }
}

void free_values(int count, Value **values) {
    int i;
    for (i = 0; i < count; i++) {
        free_value(values[i]);
    }
    free_memory(values);
}

void free_valuelist(ValueList *list) {
    /* u seznamu musime nejdrive uvolnit jeho obsah */

    if (list) {
        int i;
        for (i = 0; i < list->length; i++) {
            free_value(list->values[i]);
        }
        free_memory(list->values);
        free_memory(list);
    }
}

Value * copy_value(Value *v) {
    if (v == NULL)
        return NULL;

    switch (v->type) {
        case TYPE_LOGICAL:
            return as_logical(v) ? value_logical_T() : value_logical_NIL();
        case TYPE_SYMBOL:
            return value_symbol(as_symbol(v));
        case TYPE_EXCEPTION:
            return value_exception(as_exception(v));
        case TYPE_INTEGER:
            return value_integer(as_integer(v));
        case TYPE_LIST:
            return value_list(as_list(v)->length, as_list(v)->values);
        case TYPE_FUNCTION:
            return value_function(as_function(v));
        case TYPE_SPECIAL_FORM:
            return value_special_form(as_special_form(v));
    }

    return NULL;
}

/* mapovaci funkce */

int as_logical(Value *v) {
    assert(v != NULL);
    assert(v->type == TYPE_LOGICAL);
    return *((int *) v->pointer);
}

char * as_symbol(Value *v) {
    assert(v != NULL);
    assert(v->type == TYPE_SYMBOL);
    return (char *) v->pointer;
}

char * as_exception(Value *v) {
    assert(v != NULL);
    assert(v->type == TYPE_EXCEPTION);
    return (char *) v->pointer;
}

int as_integer(Value *v) {
    assert(v != NULL);
    assert(v->type == TYPE_INTEGER);
    return *((int *) v->pointer);
}

ValueList * as_list(Value *v) {
    assert(v != NULL);
    assert(v->type == TYPE_LIST);
    return (ValueList *) v->pointer;
}

Function as_function(Value *v) {
    assert(v != NULL);
    assert(v->type == TYPE_FUNCTION);
    return ((FunctionWrapper *) v->pointer)->function;
}

SpecialForm as_special_form(Value *v) {
    assert(v != NULL);
    assert(v->type == TYPE_SPECIAL_FORM);
    return ((SpecialFormWrapper *) v->pointer)->function;
}
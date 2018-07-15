/*
 * Obsahuje funkce pro praci s konzoli.
 *
 * File:   console.c
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
#include "console.h"


int console_read_line (char *buffer, int buffer_size) {
    int c, extra;

    /* nacteni vstupu do bufferu */
    if (fgets (buffer, buffer_size, stdin) == NULL)
        return CONSOLE_NO_INPUT;

    if (buffer[strlen(buffer) - 1] != '\n') {
        extra = 0;
        while (((c = getchar()) != '\n') && (c != EOF))
            extra = 1;

        return (extra == 1) ? CONSOLE_TOO_LONG : CONSOLE_OK;
    }

    /* nahrazení \n koncen retezce */
    buffer[strlen(buffer) - 1] = '\0';
    return CONSOLE_OK;
}

static int input_count = 0;

void print_new_input() {
    printf(FORMAT_NEW_INPUT, ++input_count);
}

static void print_indent(int indent) {
    int i;
    for(i = 0; i < indent; i++)
        printf(FORMAT_S_EXPRESSION_INDENT);
}

void print_expr(SExpression *exp, int indent) {
	int i;

    assert(indent >= 0);
	if (!exp) return;

	switch(exp->type) {
	case S_LIST:
		print_indent(indent);
		puts("(");
		for (i = 0; i < exp->length; i++) {
			print_expr(((SExpression **) exp->pointer)[i], indent + 1);
        }
		print_indent(indent);
		puts(")");
		return;
	case S_TOKEN:
		print_indent(indent);
		printf("%s\n", (char *) exp->pointer);
		return;
	}
}

/** Vypise do konzole seznam. */
static void print_list(Value *v) {
    ValueList *list = as_list(v);
    int i;

    assert(v->type == TYPE_LIST);

    printf("(");
    for (i = 0; i < list->length; i++) {
        if (i != 0) printf(" ");
        print_value(list->values[i]);
    }
    printf(")");
}

/** Vypise do konzole adresu pointeru na funkci. */
static void print_function_pointer(Function func) {
    unsigned char *p = (unsigned char *) &func;
    size_t i;

    assert(func != NULL);

    for (i = 0; i < sizeof(func); i++) {
        printf("%02x", p[i]);
    }
}

/** Vypise do konzole adresu pointeru na funkci. */
static void print_special_form_pointer(SpecialForm func) {
    unsigned char *p = (unsigned char *) &func;
    size_t i;

    assert(func != NULL);

    for (i = 0; i < sizeof(func); i++) {
        printf("%02x", p[i]);
    }
}

void print_value(Value *v) {
    if (v == NULL) {
        /* to by se nemelo stavat - asi se jedna o nejakou chybu */
        printf("Error: null value");
        return;
    }

    switch (v->type) {
        case TYPE_LOGICAL:
            printf("%s", as_logical(v) ? "T" : "NIL");
            break;
        case TYPE_SYMBOL:
            printf("%s", as_symbol(v));
            break;
        case TYPE_EXCEPTION:
            printf("Exception: %s", as_exception(v));
            break;
        case TYPE_INTEGER:
            printf("%d", as_integer(v));
            break;
        case TYPE_LIST:
            print_list(v);
            break;
        case TYPE_FUNCTION:
            printf("Built-in function: @");
            print_function_pointer(as_function(v));
            break;
        case TYPE_SPECIAL_FORM:
            printf("Built-in special form: @");
            print_special_form_pointer(as_special_form(v));
            break;
    }
}
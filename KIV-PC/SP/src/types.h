/*
 * File:   types.h
 * Author: Patrik Harag
 *
 * Created: 2016-11-06
 */

#ifndef TYPES_H
#define	TYPES_H


/* --- s-expressions --- */

/**
 * Enum pro typy v s-vyrazu.
 */
typedef enum { S_LIST, S_TOKEN } SType;

/**
 * Struktura pro s-vyraz.
 */
typedef struct _SExpression {
    SType type;
    int length;
    void *pointer;
} SExpression;

SExpression * sexpr_new(SType type, int length, void *pointer);

void free_sexpr(SExpression *exp);

void free_sexprs(int count, SExpression ** array);


/* --- datove typy ---  */

#define TYPE_LOGICAL 0
#define TYPE_INTEGER 1
#define TYPE_SYMBOL 2
#define TYPE_EXCEPTION 3
#define TYPE_LIST 4
#define TYPE_FUNCTION 5  /* funkce napsana v C */
#define TYPE_SPECIAL_FORM 6  /* funkce napsana v C, spec. pravidla pro vyhodnoceni */

/**
 * Struktura uchovavajici hodnotu a jeji typ.
 */
typedef struct _Value {
    int type;
    void *pointer;
} Value;

/**
 * Struktura pro seznam hodnot. (Datovy typ list.)
 */
typedef struct _ValueList {
    int length;
    Value **values;
} ValueList;

/**
 * Predpis pro nativni funkci napsanou v C.
 * Funkce se nestara o uvolnovani pameti predanych parametru.
 * Funkce musi vracet vzdy novou hodnotu!
 */
typedef Value * (*Function)(int argc, Value **argv);

/** Struktura pro zabaleni pointeru na funkci. */
typedef struct _FunctionWrapper {
    Function function;
} FunctionWrapper;

/**
 * Predpis pro specialni formu. Specialni forma muze podminovat vyhodnocovani.
 * Jinak pro ni plati stejna pravidla jako pro normalni funkce.
 */
typedef Value * (*SpecialForm)(int count, SExpression **exprs);

/** Struktura pro zabaleni pointeru na funkci. */
typedef struct _SpecialFormWrapper {
    SpecialForm function;
} SpecialFormWrapper;

/* vytvareni hodnot */

Value * value_new(int type, void *pointer);
ValueList * valuelist_new(int length, Value **values, boolean deep_copy);

Value * value_logical_NIL();
Value * value_logical_T();
Value * value_integer(int integer);
Value * value_symbol(char * symbol);
Value * value_exception(char * message);
Value * value_list(int argc, Value **argv);
Value * value_function(Function f);
Value * value_special_form(SpecialForm f);

/* dalsi funkce pro praci s hodnotami */

void free_value(Value *v);
void free_values(int count, Value **values);
void free_valuelist(ValueList *list);
Value * copy_value(Value *v);

/* funkce pro ziskani hodnot */

int as_logical(Value *v);
int as_integer(Value *v);
char * as_symbol(Value *v);
char * as_exception(Value *v);
ValueList * as_list(Value *v);
Function as_function(Value *v);
SpecialForm as_special_form(Value *v);

/* konstanty */

#define NIL value_logical_NIL()
#define T value_logical_T()

#endif	/* TYPES_H */
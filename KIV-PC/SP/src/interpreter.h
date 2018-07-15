/*
 * File:   interpreter.h
 * Author: Patrik Harag
 *
 * Created: 2016-11-06
 */

#ifndef INTERPRETER_H
#define	INTERPRETER_H

#define MAX_FUNCTION_PARAMETERS 32
#define EXCEPTION_OCCURED -1

#define MSG_NOT_A_FUNCTION "not a function"
#define MSG_UNKNOWN_IDENTIFIER "unknown identifier"
#define MSG_WRONG_NUMBER_FORMAT "wrong number format"

/**
 * Vyhodnoti dany s-vyraz.
 *
 * @param exp s-vyraz
 * @return vysledek
 */
Value * eval_statement(SExpression *exp);

/**
 * Inicializuje interpreter - tj. predevsim nacte vestavene funkce a konstanty.
 */
void interpreter_init();

/**
 * Uvolni pamet.
 */
void interpreter_shutdown();

#endif	/* INTERPRETER_H */
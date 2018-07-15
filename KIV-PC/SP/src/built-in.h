/*
 * Soubor definujici vestavene funkce jazyka.
 *
 * File:   built-in.h
 * Author: Patrik Harag
 *
 * Created: 2016-11-09
 */

#ifndef BUILT_IN_H
#define	BUILT_IN_H

#define MSG_WRONG_TYPE "wrong value type"
#define MSG_TOO_FEW_ARGUMENTS "too few arguments"
#define MSG_TOO_MANY_ARGUMENTS "too many arguments"
#define MSG_OUT_OF_BOUNDS "index out of bounds"

/**
 * Inicializuje vestavene funkce a konstanty.
 */
void builtin_init();

#endif	/* BUILT_IN_H */
/*
 * File:   reader.h
 * Author: Patrik Harag
 *
 * Created: 2016-11-14
 */

#ifndef READER_H
#define	READER_H

#define BUFFER_DEF_SIZE 32
#define BUFFER_INC_RATIO 2

#define S_EXPRESSION_DEF_SIZE 4
#define S_EXPRESSION_INC_RATIO 2

/**
 * Sestavi AST ze vstupniho retezce.
 */
SExpression * build_s_expressions(char *input);

/**
 * Sestavi AST z tokenu.
 */
SExpression * parse(int count, char **tokens);

/* makra */

#define QUOTE_CHAR '\''
#define QUOTE_CHAR_STR "'"
#define QUOTE_FUNCTION "QUOTE"

/* chybova hlaseni */

#define ERROR_L_PAR_EXPECTED 1
#define ERROR_L_PAR_EXPECTED_MSG "( expected"

#define ERROR_R_PAR_EXPECTED 2
#define ERROR_R_PAR_EXPECTED_MSG ") expected"

#define ERROR_QUOTE_ALONE 3
#define ERROR_QUOTE_ALONE_MSG "\\' can't be alone"

#define ERROR_UNKNOWN_MSG "unknown error"

/** Vrati boolean podle toho, zda došlo k syntakticke chybe. */
boolean reader_has_error();

/** Vypise do konzole chybovou hlasku. */
void print_reader_error();

#endif	/* READER_H */
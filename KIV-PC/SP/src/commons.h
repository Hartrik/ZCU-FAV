/*
 * File:   commons.h
 * Author: Patrik Harag
 *
 * Created: 2016-11-08
 */

#ifndef COMMONS_H
#define	COMMONS_H

#define DEBUG 0


/** Logicky datovy typ. */
typedef enum { false, true } boolean;


/**
 * Uvolni pamet.
 *
 * @param ptr pointer
 */
void free_memory(void *ptr);

/**
 * Alokuje pamet a vrati na ni pointer.
 *
 * @param count pocet jednotek (pole)
 * @param size velikost jednotky
 * @return pointer na nove alokovanou pamet
 */
void * get_memory(const int count, const int size);

/**
 * Vrati pocet aktualne alokovanych objektu.
 *
 * @return int
 */
int get_allocated_objects();

/**
 * Zkopiruje retezec.
 *
 * @param text retezec
 * @return zkopirovany retezec
 */
char * copy_string(char *text);

/**
 * Uvolni pole retezcu.
 *
 * @param count delka pole
 * @param strings pole
 */
void free_strings(int count, char **strings);

#endif	/* COMMONS_H */
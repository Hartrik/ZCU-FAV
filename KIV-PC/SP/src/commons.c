/*
 * File:   commons.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-08
 */

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#include "commons.h"


/* --- PRACE S PAMETI --- */

/* Pocet alokovanych objektu. */
static int ALLOCATED_OBJECTS = 0;

void free_memory(void *ptr) {
    if (ptr != NULL) {
        ALLOCATED_OBJECTS--;
        free(ptr);
    }
}

void * get_memory(const int count, const int size) {
    assert(count >= 0 && size >= 0);

    if (count == 0)
        return NULL;

    ALLOCATED_OBJECTS++;
    return calloc(count, size);
}

int get_allocated_objects() {
    return ALLOCATED_OBJECTS;
}

/* --- OSTATNI --- */

char * copy_string(char *text) {
    int length;
    char *copy;

    length = strlen(text);
    copy = (char *) get_memory(length + 1 /* \0 */, sizeof(char));

    strncpy(copy, text, length);
    return copy;
}

void free_strings(int count, char **strings) {
    int i;
    for (i = 0; i < count; i++) {
        free_memory(strings[i]);
    }
    free_memory(strings);
}
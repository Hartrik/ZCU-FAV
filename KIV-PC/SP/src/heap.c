/*
 * File:   heap.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-09
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include "commons.h"
#include "types.h"
#include "heap.h"


/**
 * Struktura pro polozku v halde.
 */
typedef struct _HeapEntry {
    char *name;
    Value *value;
    boolean constant;
} HeapEntry;

static HeapEntry **heap = NULL;
static int heap_size = 0;
static int heap_capacity = 0;

/**
 * Zajisti potrebnou velikost haldy pro pridani dalsiho prvku.
 */
static void ensure_capacity() {
    if (heap_capacity >= heap_size + 1)
        return;  /* zatim neni potreba zvetsovat */

    if (heap == NULL) {
        heap_capacity = HEAP_DEFAULT_CAPACITY;
        heap = (HeapEntry **) get_memory(heap_capacity, sizeof(HeapEntry*));
    } else  {
        heap_capacity *= HEAP_INCREASE_RATIO;
        heap = (HeapEntry **) realloc(heap, heap_capacity * sizeof(HeapEntry*));
    }
}

/**
 * Vrati index prvku v halde podle zadaneho jmena.
 * Zapornou hodnotu, pokud takový prvek neexistuje.
 */
static int heap_index_of(char *name) {
    int i;

    assert(name != NULL);

    for (i = 0; i < heap_size; i++) {
        HeapEntry *entry = heap[i];

        if (strcmp(name, entry->name) == 0) {
            return i;
        }
    }

    return -1;
}

Value * heap_get(char *name) {
    int i = heap_index_of(name);
    return (i >= 0) ? copy_value(heap[i]->value) : NULL;
}

boolean heap_set(char *name, Value *value, boolean constant) {
    int i = heap_index_of(name);
    HeapEntry *entry;

    if (i < 0) {
        /* pridavame na haldu novou hodnotu */

        heap_size++;

        entry = (HeapEntry *) get_memory(1, sizeof(HeapEntry));
        entry->name = get_memory(strlen(name) + 1, sizeof(char));
        entry->value = value;
        entry->constant = constant;

        strcpy(entry->name, name);

        ensure_capacity();
        heap[heap_size - 1] = entry;
        return true;

    } else {
        /* menime hodnotu na halde */

        entry = heap[i];

        if (entry->constant)
            return false;  /* neni mozne menit hodnotu konstanty */

        entry->value = value;
        entry->constant = constant;
        return true;
    }
}

void heap_init() {
    heap_free();
}

void heap_free() {
    if (heap_size != 0) {
        int i;

        for (i = 0; i < heap_size; i++) {
            HeapEntry *entry = heap[i];

            free_memory(entry->name);
            free_value(entry->value);
            free_memory(entry);
        }

        free_memory(heap);
    }

    heap = NULL;
    heap_size = 0;
    heap_capacity = 0;
}
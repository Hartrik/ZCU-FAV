/*
 * File:   heap.h
 * Author: Patrik Harag
 *
 * Created: 2016-11-09
 */

#ifndef HEAP_H
#define	HEAP_H

#define HEAP_DEFAULT_CAPACITY 64
#define HEAP_INCREASE_RATIO 2


/**
 * Vrati hodnotu ulozenou na halde nebo NULL.
 *
 * @param name jmeno promenne
 * @return pointer na hlubokou kopii hodnoty
 */
Value * heap_get(char *name);

/**
 * Ulozi hodnotu na haldu. Uklada si pointer primo, nevytvari kopii hodnoty.
 *
 * @param name jmeno promenne
 * @param value pointer na hodnotu
 * @return uspesnost nastaveni promenne (selze pri pokusu prepsat konstantu)
 */
boolean heap_set(char *name, Value *value, boolean constant);

/**
 * Inicializuje haldu.
 */
void heap_init();

/**
 * Uvolni haldu.
 */
void heap_free();

#endif	/* HEAP_H */
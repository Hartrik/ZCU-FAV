
/**
 * Definuje různé obecné utility funkce.
 *
 * @author: Patrik Harag
 * @version: 2017-12-20
 */

#ifndef KIV_ZOS_UTILS_H
#define KIV_ZOS_UTILS_H

#include <stdbool.h>


/** Výčtový typ který informuje, jakým způsobem dopadlo volání nějaké funkce */
typedef enum {
    RESULT_OK,
    RESULT_WARNING,  /* malá chyba, nechá se z ní vzpamatovat */
    RESULT_ERROR,    /* podstatná chyba, je nutné ukončit program  */
    RESULT_EXIT
} Result;

#define utils_max(x, y) (((x) > (y)) ? (x) : (y))
#define utils_min(x, y) (((x) < (y)) ? (x) : (y))

/** Vydělí a / b, vrátí výsledek zaokrouhlený nahoru */
int32_t utils_div_ceil(int32_t a, int32_t b);

/** Rozdělí řetězec podle mezery */
char** utils_split_by_space(char* str, int* out_count);

/** Vrátí true, pokud je bit nastaven na 1 */
bool utils_is_bit_set(unsigned char value, int pos);
/** Nastaví bit na 1 */
unsigned char utils_set_bit(unsigned char value, int pos);
/** Nastaví bit na 0 */
unsigned char utils_unset_bit(unsigned char value, int pos);

#endif

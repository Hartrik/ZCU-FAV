
/**
 * Implementuje utility funkce.
 *
 * @author: Patrik Harag
 * @version: 2017-12-20
 */

#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include "utils.h"

int32_t utils_div_ceil(int32_t a, int32_t b) {
    int32_t result = a / b;
    if (a % b != 0) {
        result++;
    }
    return result;
}

char** utils_split_by_space(char* str, int* out_count) {
    char** out = NULL;
    char* p = strtok(str, " ");
    int n = 0;

    while (p) {
        out = realloc(out, sizeof (char*) * ++n);
        out[n-1] = p;

        p = strtok(NULL, " ");
    }

    *out_count = n;
    return out;
}

bool utils_is_bit_set(unsigned char value, int pos) {
    assert(pos >= 0 && pos < 8);
    return (value & (1 << pos)) != 0;
}

unsigned char utils_set_bit(unsigned char value, int pos) {
    assert(pos >= 0 && pos < 8);
    return (unsigned char) ((1 << pos) | value);
}

unsigned char utils_unset_bit(unsigned char value, int pos) {
    assert(pos >= 0 && pos < 8);
    return (unsigned char) (value & ~(1 << pos));
}

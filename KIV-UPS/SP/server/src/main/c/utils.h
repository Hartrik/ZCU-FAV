
/**
 * Contains utility functions.
 *
 * @author: Patrik Harag
 * @version: 2017-10-09
 */

#include <stdbool.h>

#ifndef SERVER_UTILS_H
#define SERVER_UTILS_H

/**
 * Pause execution for a number of milliseconds.
 */
int utils_sleep(unsigned long ms);

/**
 * Returns current time in millis.
 */
unsigned long long utils_current_millis();

bool utils_is_valid_name(char* string);

#endif


/**
 * Structure for storing messages.
 *
 * @author: Patrik Harag
 * @version: 2017-10-29
 */

#ifndef SERVER_BUFFER_H
#define SERVER_BUFFER_H

#define BUFFER_INC_RATIO 2
#define BUFFER_MIN_CAPACITY 2

typedef struct _Buffer {
    size_t index;
    size_t content_length;
    char *content;
} Buffer;

/**
 * Init buffer.
 *
 * @param buffer
 * @param length
 */
void buffer_init(Buffer *buffer, size_t length);

/**
 * Frees buffer.
 *
 * @param buffer
 */
void buffer_free(Buffer *buffer);

/**
 * Resets buffer.
 *
 * @param buffer
 */
void buffer_reset(Buffer* buffer);

/**
 * Shifts buffer left.
 *
 * @param buffer
 * @param n
 */
void buffer_shift_left(Buffer* buffer, int n);

/**
 * Adds char at the end of a buffer.
 *
 * @param buffer
 * @param char
 */
void buffer_add(Buffer *buffer, char c);

/**
 * Adds string at the end of a buffer.
 *
 * @param buffer
 * @param string
 */
void buffer_add_string(Buffer *buffer, char* string);

// MESSAGE BUFFER

/**
 * Gets message type from a buffer.
 *
 * @param buffer
 * @return type
 */
char* message_buffer_get_type(Buffer* buffer);

/**
 * Gets message content from a buffer.
 *
 * @param buffer
 * @return content
 */
char* message_buffer_get_content(Buffer* buffer);

#endif

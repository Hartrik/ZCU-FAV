
/**
 * Structure for storing messages.
 *
 * @author: Patrik Harag
 * @version: 2017-10-29
 */

#include <stdlib.h>
#include <string.h>
#include "buffer.h"
#include "protocol.h"


void buffer_init(Buffer *buffer, size_t length) {
    if (length < BUFFER_MIN_CAPACITY)
        length = BUFFER_MIN_CAPACITY;

    buffer->index = 0;
    buffer->content_length = length;
    buffer->content = malloc(length);
}

void buffer_free(Buffer *buffer) {
    free(buffer->content);
}

void buffer_reset(Buffer* buffer) {
    buffer->index = 0;
}

void buffer_shift_left(Buffer* buffer, int n) {
    if (n == 0) return;

    for (int i = 0; i < buffer->index - n; ++i) {
        buffer->content[i] = buffer->content[i + n];
    }
    buffer->index -= n;
}

void buffer_add(Buffer *buffer, char c) {
    if (buffer->index >= buffer->content_length) {
        /* we have to increase the buffer */
        size_t new_size = buffer->content_length * BUFFER_INC_RATIO;
        buffer->content = realloc(buffer->content, new_size);
        buffer->content_length = new_size;
    }

    buffer->content[buffer->index++] = c;
}

void buffer_add_string(Buffer *buffer, char* string) {
    // TODO: memset
    size_t length = strlen(string);
    for (int i = 0; i < length; ++i) {
        buffer_add(buffer, string[i]);
    }
}

// MESSAGE BUFFER

char* message_buffer_get_type(Buffer *buffer) {
    return buffer->content;
}

char* message_buffer_get_content(Buffer* buffer) {
    int pos = PROTOCOL_TYPE_SIZE + 1;

    if (pos < buffer->index)
        return buffer->content + pos;
    else
        return NULL;  // no content
}
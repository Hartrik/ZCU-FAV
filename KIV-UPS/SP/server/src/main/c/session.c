
/**
 * Contains structure for storing data about one connection and its functions.
 *
 * @author: Patrik Harag
 * @version: 2017-10-29
 */

#include <memory.h>
#include "session.h"

void session_init(Session *session) {
    session->id = -1;
    session->status = SESSION_STATUS_CONNECTED;
    session->socket_fd = -1;
    session->last_activity = 0;
    session->last_ping = 0;
    session->corrupted_messages = 0;
    memset(session->name, 0, sizeof(session->name));
    session->game = NULL;
    buffer_init(&(session->to_send), SESSION_BUFFER_DEFAULT_CAPACITY);
}

void session_free(Session* session) {
    buffer_free(&(session->to_send));
}

bool session_is_active(Session *session) {
    return session->status == SESSION_STATUS_CONNECTED;
}

bool session_is_logged(Session* session) {
    return session_is_active(session) && session->name[0] != 0;
}

bool session_is_in_game(Session* session) {
    return session_is_logged(session) && session->game != NULL;
}

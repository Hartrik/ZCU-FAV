
/**
 * Contains structure for storing data about one connection and its functions.
 *
 * @author: Patrik Harag
 * @version: 2017-10-29
 */

#ifndef SERVER_SESSION_H
#define SERVER_SESSION_H

#include <stdbool.h>
#include "game.h"
#include "buffer.h"

#define SESSION_BUFFER_DEFAULT_CAPACITY 8
#define SESSION_PLAYER_MIN_NAME_LENGTH 1
#define SESSION_PLAYER_MAX_NAME_LENGTH 12

typedef enum {
    SESSION_STATUS_CONNECTED,
    SESSION_STATUS_SHOULD_DISCONNECT,
    SESSION_STATUS_DISCONNECTED
} SessionStatus;

/**
 * Holds information about one connection.
 */
typedef struct _Session {

    int id;
    SessionStatus status;

    int socket_fd;
    unsigned long long last_activity;
    unsigned long long last_ping;
    Buffer to_send;
    int corrupted_messages;

    char name[SESSION_PLAYER_MAX_NAME_LENGTH + 1];
    Game* game;

} Session;

/** Init session. */
void session_init(Session *session);

/** Free session. */
void session_free(Session* session);

bool session_is_active(Session *session);
bool session_is_logged(Session* session);
bool session_is_in_game(Session* session);

#endif

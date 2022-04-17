
/**
 * Socket server, handles multiple clients.
 *
 * @author: Patrik Harag
 * @version: 2017-10-29
 */

#ifndef SERVER_SERVER_H
#define SERVER_SERVER_H

#include <stdbool.h>
#include "session.h"

#define SERVER_SOCKET_BUFFER_SIZE 256
#define SERVER_MSG_BUFFER_SIZE 256
#define SERVER_CONNECTION_QUEUE 10  /* how many pending connections will be held */
#define SERVER_TIMEOUT 5000  /* ms */
#define SERVER_CYCLE 20  /* ms */
#define SERVER_CYCLE_LONG 100  /* ms */
#define SERVER_MAX_CORRUPTED_MESSAGES 50  /* per session */
#define SERVER_MAX_MESSAGE_SIZE 1024

bool TERMINATED;

int server_start(int port);
void server_read(Session* session, char* input, long input_size, Buffer* message_buffer);

#endif //SERVER_SERVER_H

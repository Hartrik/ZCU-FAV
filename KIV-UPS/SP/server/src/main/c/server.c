
/**
 * Socket server, handles multiple clients.
 *
 * @author: Patrik Harag
 * @version: 2017-10-29
 */

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>

#include "server.h"
#include "utils.h"
#include "protocol.h"
#include "controller.h"
#include "stats.h"
#include "shared.h"


static void* game_thread_handler(void *);
static void process_session(Session* session, char socket_buffer[SERVER_SOCKET_BUFFER_SIZE], Buffer* message_buffer);

static bool set_non_blocking(int fd) {
    // Set accept to be non-blocking
    int flags = fcntl(fd, F_GETFL, 0);
    if (flags < 0) {
        perror("Could not get fd flags");
        return false;
    }

    int err = fcntl(fd, F_SETFL, flags | O_NONBLOCK);
    if (err < 0) {
        perror("Could not set fd to be non blocking");
        return false;
    }

    return true;
}

static void handle_error(Session* session) {
    if ((errno != EAGAIN) && (errno != EWOULDBLOCK)) {
        if (errno != EPIPE) {
            printf("  [%d] Error", session->id);
            perror("");
        }
        session->status = SESSION_STATUS_SHOULD_DISCONNECT;
    }
}

int server_start(int port) {
    // Create socket
    int server_fd = socket(AF_INET, SOCK_STREAM, IPPROTO_IP /* TPC */);
    if (server_fd == -1) {
        printf("Could not create server socket\n");
    }
    printf("Server socket created\n");

    // Fix /Address already in use/ error
    int option = 1;
    setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &option, sizeof(option));

    struct sockaddr_in server;
    server.sin_family = AF_INET;
    server.sin_addr.s_addr = INADDR_ANY;
    server.sin_port = htons(port);

    // Bind
    if (bind(server_fd, (struct sockaddr *) &server, sizeof(server)) < 0) {
        printf("Bind failed (port=%d)\n", port);
        perror("Error");
        return 1;
    }
    printf("Bind done, port=%d\n", port);

    // Set accept to be non-blocking
    if (!set_non_blocking(server_fd))
        return 1;

    // Listen
    if (listen(server_fd, SERVER_CONNECTION_QUEUE) < 0) {
        perror("Listen failed");
        return 1;
    }

    // Create game thread
    pthread_t thread;
    if (pthread_create(&thread, NULL, game_thread_handler, NULL) < 0) {
        perror("Could not create thread");
        return 1;
    }

    printf("Waiting for incoming connections...\n");

    do {
        struct sockaddr client;
        socklen_t client_len = sizeof(client);

        int client_fd = accept(server_fd, &client, &client_len);

        if (client_fd > 0) {
            stats_add_connections_established(1);
            printf("Connection accepted\n");

            // Set recv and write to be non-blocking
            if (!set_non_blocking(client_fd))
                return 1;

            pthread_mutex_lock(&shared_lock);
            shared_create_session(client_fd);
            pthread_mutex_unlock(&shared_lock);
        } else {
            utils_sleep(50);
        }
    } while (!TERMINATED);

    close(server_fd);
    printf("Server socket closed\n");

    pthread_join(thread, NULL);
    printf("Game thread closed\n");

    return 0;
}

/**
 * Game thread handler.
 */
void* game_thread_handler(void *arg) {
    // Set up socket
    char socket_buffer[SERVER_SOCKET_BUFFER_SIZE];

    // Set up message buffer
    Buffer message_buffer;
    buffer_init(&message_buffer, SERVER_MSG_BUFFER_SIZE);

    printf("  Game thread active...\n");

    while (!TERMINATED) {
        pthread_mutex_lock(&shared_lock);

        unsigned long long cycle_start = utils_current_millis();

        // process all active sessions
        for (int i = 0; i < session_pool.sessions_size; ++i) {
            Session* session = session_pool.sessions[i];

            if (session->status == SESSION_STATUS_CONNECTED) {
                process_session(session, socket_buffer, &message_buffer);
            }

            if (session->status == SESSION_STATUS_SHOULD_DISCONNECT) {
                session->status = SESSION_STATUS_DISCONNECTED;

                close(session->socket_fd);

                printf("  [%d] Client disconnected\n", session->id);
            }
        }

        pthread_mutex_unlock(&shared_lock);

        unsigned long long cycle_end = utils_current_millis();
        unsigned long long cycle = cycle_end - cycle_start;
        if (cycle < SERVER_CYCLE) {
            utils_sleep(SERVER_CYCLE - cycle);
        } else if (cycle > SERVER_CYCLE_LONG) {
            printf("  Long cycle: %llu ms\n", cycle);
        }
    }

    buffer_free(&message_buffer);
    return 0;
}

static void process_session(Session* session, char socket_buffer[SERVER_SOCKET_BUFFER_SIZE],
                            Buffer* message_buffer) {

    int socket_fd = session->socket_fd;
    unsigned long long cycle_start = utils_current_millis();

    controller_update_session(session, cycle_start);

    // write
    if (session->to_send.index > 0) {
        ssize_t written = write(socket_fd, session->to_send.content, session->to_send.index);
        if (written > 0) {
            stats_add_bytes_sent(written);

            printf("  [%d] << %d B\n", session->id,
                   (int) session->to_send.index);

            if (session->to_send.index == written) {
                buffer_reset(&session->to_send);
            } else {
                printf("  [%d] not everything sent\n", session->id);
                buffer_shift_left(&session->to_send, (int) written);
            }

        } else {
            // end of stream or timeout
            handle_error(session);
        }
    }

    // read
    ssize_t recv_size = recv(socket_fd, socket_buffer, SERVER_SOCKET_BUFFER_SIZE, 0);
    if (recv_size > 0) {
        stats_add_bytes_received(recv_size);

        if (message_buffer->index + recv_size > SERVER_MAX_MESSAGE_SIZE) {
            printf("  [%d] Message too long\n", session->id);
            session->status = SESSION_STATUS_SHOULD_DISCONNECT;
            return;
        }

        server_read(session, socket_buffer, recv_size, message_buffer);

    } else if (recv_size == -1) {
        // end of stream or timeout
        handle_error(session);

    } else {
        // no data...
    }

    // timeout
    unsigned long long cycle_end = utils_current_millis();
    unsigned long long diff = cycle_end - session->last_activity;
    if (diff > SERVER_TIMEOUT) {
        printf("  [%d] Timeout (after %llu ms)\n", session->id, diff);
        session->status = SESSION_STATUS_SHOULD_DISCONNECT;
    }

    // corrupted messages
    if (session->corrupted_messages > SERVER_MAX_CORRUPTED_MESSAGES) {
        printf("  [%d] Too many corrupted messages\n", session->id);
        session->status = SESSION_STATUS_SHOULD_DISCONNECT;
    }
}

void server_read(Session* session, char* input, long input_size, Buffer* message_buffer) {
    session->last_activity = utils_current_millis();

    for (int i = 0; i < input_size; ++i) {
        char c = input[i];
        if (c == PROTOCOL_MESSAGE_SEP) {
            // end of message
            if (message_buffer->index != 0) {
                buffer_add(message_buffer, 0);

                char *type = message_buffer_get_type(message_buffer);
                char *content = message_buffer_get_content(message_buffer);

                controller_process_message(session, type, content);

                // reset buffer
                buffer_reset(message_buffer);
            }
        } else {
            if (message_buffer->index == PROTOCOL_TYPE_SIZE) {
                // split message type and its content
                buffer_add(message_buffer, 0);
            }

            buffer_add(message_buffer, input[i]);
        }
    }
}


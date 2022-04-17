
/**
 * Manages shared game state.
 *
 * @author: Patrik Harag
 * @version: 2017-10-29
 */

#include <pthread.h>
#include <stdio.h>
#include "shared.h"
#include "utils.h"
#include "server.h"


void shared_init() {
    if (pthread_mutex_init(&shared_lock, NULL) != 0) {
        perror("Mutex init failed");
    }

    gp_init(&game_pool);
    sp_init(&session_pool);
}

Session *shared_create_session(int socket_fd) {
    Session* session = sp_create(&session_pool);
    session->socket_fd = socket_fd;
    session->last_activity = utils_current_millis();
    return session;
}

void shared_load() {
    FILE* file = fopen(SHARED_FILE, "r");

    if (file == NULL) {
        printf("Save file not found: %s\n", SHARED_FILE);
        return;
    }

    printf("Loading: '%s'\n", SHARED_FILE);
    Session* dummy_session = shared_create_session(0);

    Buffer message_buffer;
    buffer_init(&message_buffer, 256);

    char input[1024];
    size_t input_size;
    while ((input_size = fread(input, 1, sizeof(input), file)) > 0) {
        server_read(dummy_session, (char *) &input, input_size, &message_buffer);
    }

    dummy_session->status = SESSION_STATUS_DISCONNECTED;

    fclose(file);
    printf("Loading complete\n");
}

void shared_store() {
    FILE *f = fopen(SHARED_FILE, "w");
    if (f == NULL) {
        perror("Error while creating save file");
        return;

    } else {
        // login dummy user
        fprintf(f, "|LINsystem");

        for (int i = 0; i < game_pool.games_size; ++i) {
            Game* game = game_pool.games[i];

            // create game
            fprintf(f, "|GNW%d,%d", game->w, game->h);

            // join game
            fprintf(f, "|GJO%d", game->id);

            // move pieces
            fprintf(f, "|GAC");
            for (int j = 0; j < game->w * game->h; ++j) {
                Piece* piece = game->pieces[j];
                fprintf(f, "%d,%d,%d;", piece->id, piece->x, piece->y);
            }

            // left game
            fprintf(f, "|GOF");
        }
        // close "connection"
        fprintf(f, "|BYE|");

        fclose(f);
        printf("Created '%s'\n", SHARED_FILE);
    }
}

void shared_free() {
    sp_free(&session_pool);
    gp_free(&game_pool);
    pthread_mutex_destroy(&shared_lock);
}

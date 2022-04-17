
/**
 * Process received messages.
 *
 * @author: Patrik Harag
 * @version: 2017-10-29
 */

#include <stdio.h>
#include <assert.h>
#include <string.h>
#include <stdbool.h>
#include "controller.h"
#include "protocol.h"
#include "utils.h"
#include "server.h"
#include "shared.h"
#include "stats.h"

static void check_timeout(Session *session, unsigned long long time) {
    unsigned long long last_activity_diff = time - session->last_activity;

    if (last_activity_diff > (SERVER_TIMEOUT / 2)) {
        unsigned long long last_ping_diff = time - session->last_ping;
        if (last_ping_diff > (SERVER_TIMEOUT / 2)) {
            session->last_ping = time;
            controller_send(session, "PIN", "");
        }
    }
}

void controller_update_session(Session *session, unsigned long long time) {
    check_timeout(session, time);
}

void controller_update_game(Game* game) {
    if (!game->finished) {
        game_check_is_finished(game);
        if (game->finished) {
            controller_broadcast_game_finished(game);
        }
    }
}

static bool parse_number(char* string, long* i) {
    if (string == NULL)
        return false;

    char *rest;
    *i = strtol(string, &rest, 10);

    if (strlen(rest) != 0)
        return false;

    return true;
}

static void controller_process_BYE(Session *session, char *content) {
    session->status = SESSION_STATUS_SHOULD_DISCONNECT;
}

static void controller_process_PIN(Session *session, char *content) {
    // nothing
}

static void controller_process_LIN(Session *session, char *content) {
    char* name = content;

    if (session_is_logged(session)) {
        controller_send_int(session, "LIN", PROTOCOL_LIN_ALREADY_LOGGED);
    } else if (name == NULL || strlen(name) < SESSION_PLAYER_MIN_NAME_LENGTH) {
        controller_send_int(session, "LIN", PROTOCOL_LIN_NAME_TOO_SHORT);
    } else if (strlen(name) > SESSION_PLAYER_MAX_NAME_LENGTH) {
        controller_send_int(session, "LIN", PROTOCOL_LIN_NAME_TOO_LONG);
    } else if (!utils_is_valid_name(name)) {
        controller_send_int(session, "LIN", PROTOCOL_LIN_UNSUPPORTED_CHARS);
    } else if (!sp_is_free_name(&session_pool, name)) {
        controller_send_int(session, "LIN", PROTOCOL_LIN_NAME_ALREADY_IN_USE);
    } else {
        strcpy(session->name, name);
        controller_send_int(session, "LIN", PROTOCOL_LIN_OK);
        printf("  [%d] - User logged in: %s\n", session->id, session->name);
    }
}

static void controller_process_LOF(Session *session, char *content) {
    if (session_is_in_game(session)) {
        controller_send_int(session, "LOF", PROTOCOL_LOF_IN_GAME);
    } else {
        session->name[0] = 0;
        controller_send_int(session, "LOF", PROTOCOL_LOF_OK);
        printf("  [%d] - User logged out", session->id);
    }
}

static void controller_process_GLI(Session *session, char *content) {
    if (session_is_logged(session)) {

        size_t count = game_pool.games_size;

        Buffer buffer;
        buffer_init(&buffer, 3 * (count /*separators*/ + count * 4 /*digits*/));

        for (int i = 0; i < count; ++i) {
            Game* game = game_pool.games[i];
            if (game->finished) continue;

            char temp[12];

            sprintf(temp, "%d", game->id);
            buffer_add_string(&buffer, temp);
            buffer_add(&buffer, ',');
            sprintf(temp, "%d", game->w);
            buffer_add_string(&buffer, temp);
            buffer_add(&buffer, ',');
            sprintf(temp, "%d", game->h);
            buffer_add_string(&buffer, temp);
            buffer_add(&buffer, ';');
        }

        buffer_add(&buffer, '\0');
        controller_send(session, "GLI", buffer.content);

        buffer_free(&buffer);

    } else {
        controller_send(session, "GLI", "");
    }
}

static void controller_process_GPL(Session *session, char *content) {
    long id;
    if (session_is_logged(session) && parse_number(content, &id)) {

        Game* game = gp_find_game(&game_pool, (int) id);

        if (game != NULL) {
            Buffer buffer;
            buffer_init(&buffer, 16);

            for (int i = 0; i < session_pool.sessions_size; ++i) {
                Session* s = session_pool.sessions[i];
                if (s->status == SESSION_STATUS_CONNECTED && s->game == game) {
                    buffer_add_string(&buffer, s->name);
                    buffer_add(&buffer, ',');
                }
            }
            buffer_add(&buffer, '\0');
            controller_send(session, "GPL", buffer.content);

        } else {
            controller_send(session, "GPL", "");
        }

    } else {
        controller_send(session, "GPL", "");
    }
}

static void controller_process_GNW(Session *session, char *content) {
    unsigned int w = 0;
    unsigned int h = 0;

    if (!gp_can_create_game(&game_pool, session)) {
        controller_send_int(session, "GNW", PROTOCOL_GNW_NO_PERMISSIONS);
    } else if (content != NULL && sscanf(content, "%u,%u;", &w, &h)) {
        if (w >= GAME_MIN_SIZE && w <= GAME_MAX_SIZE
            && h >= GAME_MIN_SIZE && h <= GAME_MAX_SIZE) {

            Game* game = gp_create_game(&game_pool, w, h);

            controller_send_int(session, "GNW", game->id);
            printf("  [%d] - New game [id=%u, w=%u, h=%u]\n",
                   session->id, game->id, w, h);

        } else {
            controller_send_int(session, "GNW", PROTOCOL_GNW_WRONG_SIZE);
        }
    } else {
        controller_send_int(session, "GNW", PROTOCOL_GNW_WRONG_FORMAT);
    }
}

static void controller_process_GJO(Session *session, char *content) {
    if (!gp_can_join_game(&game_pool, session)) {
        controller_send_int(session, "GJO", PROTOCOL_GJO_NO_PERMISSIONS);
    } else {
        long id;
        if (parse_number(content, &id)) {
            Game* game = gp_join_game(&game_pool, session, (int) id);
            if (game != NULL) {
                controller_send_int(session, "GJO", PROTOCOL_GJO_OK);
            } else {
                controller_send_int(session, "GJO", PROTOCOL_GJO_CANNOT_JOIN);
            }

        } else {
            controller_send_int(session, "GJO", PROTOCOL_GJO_CANNOT_JOIN);
        }
    }
}

static void controller_process_GST(Session *session, char *content) {
    long id;

    if (session_is_logged(session) && parse_number(content, &id)) {
        Game* game = gp_find_game(&game_pool, (int) id);
        if (game != NULL) {
            size_t pieces = game->h * game->w;

            Buffer buffer;
            buffer_init(&buffer, 2 * (pieces /*separators*/ + pieces * 5 /*digits*/));

            for (int i = 0; i < pieces; ++i) {
                Piece* p = game->pieces[i];
                char temp[12];

                sprintf(temp, "%d", p->x);
                buffer_add_string(&buffer, temp);
                buffer_add(&buffer, ',');
                sprintf(temp, "%d", p->y);
                buffer_add_string(&buffer, temp);
                buffer_add(&buffer, ';');
            }

            buffer_add(&buffer, '\0');
            controller_send(session, "GST", buffer.content);

            buffer_free(&buffer);

        } else {
            controller_send(session, "GST", "error");
        }

    } else {
        controller_send(session, "GST", "error");
    }
}

static void controller_process_GAC(Session *session, char *content) {
    int id = 0;
    int y = 0, x = 0;

    if (!session_is_in_game(session)) {
        controller_send_int(session, "GAC", PROTOCOL_GAC_NO_PERMISSIONS);
        return;
    }

    Game* game = session->game;

    if (game->finished) {
        // ignore...
        controller_send_int(session, "GAC", PROTOCOL_GAC_OK);
        return;
    }

    // split pieces
    char* next = content;
    size_t len = strlen(content);
    for (int i = 0; i < len; ++i) {
        char c = content[i];

        if (c == ';') {
            content[i] = '\0';

            if (next == NULL) {
                // nothing

            } else if (sscanf(next, "%d,%d,%d", &id, &x, &y)) {

                if (id < 0 || id >= game->w * game->h) {
                    controller_send_int(session, "GAC", PROTOCOL_GAC_WRONG_PIECE);
                    return;

                } else {
                    Piece *piece = game->pieces[id];
                    piece->x = x;
                    piece->y = y;

                    controller_broadcast_game_action(session, game, piece);
                    printf("  [%d] - Move %d -> %d, %d\n", session->id, id, x, y);
                }

            } else {
                controller_send_int(session, "GAC", PROTOCOL_GAC_WRONG_FORMAT);
                return;
            }

            next = content + i + 1;
        }
    }
    controller_send_int(session, "GAC", PROTOCOL_GAC_OK);
    controller_update_game(game);
}

static void controller_process_GOF(Session *session, char *content) {
    session->game = NULL;
    controller_send_int(session, "GOF", PROTOCOL_GOF_OK);
}

void controller_process_message(Session *session, char *type, char *content) {
    stats_add_messages_received(1);

    printf("  [%d] Message: type='%s' content='%s'\n",
           session->id, type, content);
    fflush(stdout);

    if (strncmp(type, "BYE", 3) == 0) {
        controller_process_BYE(session, content);

    } else if (strncmp(type, "PIN", 3) == 0) {
        controller_process_PIN(session, content);

    } else if (strncmp(type, "LIN", 3) == 0) {
        controller_process_LIN(session, content);

    } else if (strncmp(type, "LOF", 3) == 0) {
        controller_process_LOF(session, content);

    } else if (strncmp(type, "GLI", 3) == 0) {
        controller_process_GLI(session, content);

    } else if (strncmp(type, "GPL", 3) == 0) {
        controller_process_GPL(session, content);

    } else if (strncmp(type, "GNW", 3) == 0) {
        controller_process_GNW(session, content);

    } else if (strncmp(type, "GJO", 3) == 0) {
        controller_process_GJO(session, content);

    } else if (strncmp(type, "GST", 3) == 0) {
        controller_process_GST(session, content);

    } else if (strncmp(type, "GAC", 3) == 0) {
        controller_process_GAC(session, content);

    } else if (strncmp(type, "GOF", 3) == 0) {
        controller_process_GOF(session, content);

    } else {
        session->corrupted_messages++;
        printf("  [%d] - Unknown command: %s\n", session->id, type);
    }
}

void controller_broadcast_game_action(Session* session, Game* game, Piece* p) {
    for (int i = 0; i < session_pool.sessions_size; ++i) {
        Session* s = session_pool.sessions[i];
        if (session_is_in_game(s) && s->game == game) {
            Buffer buffer;
            buffer_init(&buffer, 3 * (1 /*separators*/ + 5 /*digits*/));

            char temp[12];
            sprintf(temp, "%d", p->id);
            buffer_add_string(&buffer, temp);
            buffer_add(&buffer, ',');
            sprintf(temp, "%d", p->x);
            buffer_add_string(&buffer, temp);
            buffer_add(&buffer, ',');
            sprintf(temp, "%d", p->y);
            buffer_add_string(&buffer, temp);
            buffer_add(&buffer, ';');

            buffer_add(&buffer, '\0');
            controller_send(s, "GUP", buffer.content);

            buffer_free(&buffer);
        }
    }
}

void controller_broadcast_game_finished(Game* game) {
    for (int i = 0; i < session_pool.sessions_size; ++i) {
        Session* s = session_pool.sessions[i];
        if (session_is_in_game(s) && s->game == game) {
            controller_send_int(s, "GWI", game->id);
        }
    }
}

void controller_send(Session* session, char* type, char* content) {
    assert(strlen(type) == 3);
    stats_add_messages_sent(1);

    Buffer* buffer = &(session->to_send);

    buffer_add(buffer, PROTOCOL_MESSAGE_SEP);
    buffer_add_string(buffer, type);
    buffer_add_string(buffer, content);
    buffer_add(buffer, PROTOCOL_MESSAGE_SEP);
}

void controller_send_int(Session* session, char* type, int content) {
    char str[24];
    sprintf(str, "%d", content);

    controller_send(session, type, str);
}

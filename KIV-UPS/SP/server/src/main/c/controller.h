
/**
 * Process received messages.
 *
 * @author: Patrik Harag
 * @version: 2017-10-29
 */

#ifndef SERVER_CONTROLLER_H
#define SERVER_CONTROLLER_H

#include "session.h"

/** Process received message. */
void controller_process_message(Session *session, char *type, char *content);

/** Send message. */
void controller_send(Session* session, char* type, char* content);
/** Send message (number). */
void controller_send_int(Session* session, char* type, int content);

/** Broadcast that the game is finished. */
void controller_broadcast_game_finished(Game* game);
/** Broadcast game action. */
void controller_broadcast_game_action(Session* session, Game* game, Piece* p);

/** Update game state, check if the game is finished etc. */
void controller_update_game(Game* game);
/** Update session state, check timeout etc. */
void controller_update_session(Session *session, unsigned long long int i);

#endif

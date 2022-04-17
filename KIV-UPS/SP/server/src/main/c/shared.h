
/**
 * Manages shared game state.
 *
 * @author: Patrik Harag
 * @version: 2017-10-29
 */

#include "game_pool.h"
#include "session.h"
#include "session_pool.h"

#ifndef SERVER_SHARED_H
#define SERVER_SHARED_H

#define SHARED_FILE "games.pzl"

GamePool game_pool;
SessionPool session_pool;
pthread_mutex_t shared_lock;

/** Init shared state. */
void shared_init();

/** Load shared state from file (if present). */
void shared_load();

/** Create session. */
Session *shared_create_session(int i);

/** Store shared state into file. */
void shared_store();

/** Free shared state. */
void shared_free();

#endif

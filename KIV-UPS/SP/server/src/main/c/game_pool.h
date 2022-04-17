
/**
 * Manages all games at one place.
 *
 * @author: Patrik Harag
 * @version: 2017-10-28
 */

#ifndef SERVER_GAME_POOL_H
#define SERVER_GAME_POOL_H

#include <stdlib.h>
#include "game.h"
#include "session.h"

#define GAME_POOL_DEFAULT_CAPACITY 8
#define GAME_POOL_INCREASE_RATIO 2
#define GAME_POOL_MAX_GAMES 8

typedef struct _GamePool {

    Game **games;
    size_t games_size;
    size_t games_capacity;
    int last_id;

} GamePool;

/** Init game pool. */
void gp_init(GamePool* game_pool);

/** Check if a game can be created. */
bool gp_can_create_game(GamePool* game_pool, Session *session);

/** Create new game. */
Game *gp_create_game(GamePool *game_pool, unsigned int w, unsigned int h);

/** Find game by id. */
Game *gp_find_game(GamePool *game_pool, int id);

/** Check if player can join a game. */
bool gp_can_join_game(GamePool* game_pool, Session *session);

/** Join game. */
Game* gp_join_game(GamePool* game_pool, Session *session, int game_id);

/** Free game pool. */
void gp_free(GamePool* game_pool);

#endif

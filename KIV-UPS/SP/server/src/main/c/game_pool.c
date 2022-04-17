
/**
 * Manages all games at one place.
 *
 * @author: Patrik Harag
 * @version: 2017-10-29
 */

#include <stdlib.h>
#include "game_pool.h"

static void ensure_capacity(GamePool* game_pool) {
    if (game_pool->games_capacity >= game_pool->games_size + 1)
        return;  // not needed

    if (game_pool->games == NULL) {
        game_pool->games_capacity = GAME_POOL_DEFAULT_CAPACITY;
        game_pool->games = (Game **) calloc(game_pool->games_capacity, sizeof(Game*));
    } else  {
        game_pool->games_capacity *= GAME_POOL_INCREASE_RATIO;
        game_pool->games = (Game **) realloc(
                game_pool->games, game_pool->games_capacity * sizeof(Game*));
    }
}

void gp_init(GamePool* game_pool) {
    gp_free(game_pool);
}

bool gp_can_create_game(GamePool* game_pool, Session *session) {
    if (!session_is_logged(session))
        return false;

    if (session_is_in_game(session))
        return false;

    int count = 0;
    for (int i = 0; i < game_pool->games_size; ++i) {
        Game* game = game_pool->games[i];
        if (!game->finished)
            count++;
    }

    if (count >= GAME_POOL_MAX_GAMES)
        return false;

    return true;
}

Game *gp_create_game(GamePool *game_pool, unsigned int w, unsigned int h) {
    Game* game = (Game *) calloc(1, sizeof(Game));
    game_init(game, ++game_pool->last_id, w, h);
    game_shuffle(game,
                 (int) (game->w * GAME_PIECE_SIZE * GAME_SHUFFLE_MULTIPLIER * GAME_WH_RATIO / 2),
                 (int) (game->h * GAME_PIECE_SIZE * GAME_SHUFFLE_MULTIPLIER / GAME_WH_RATIO / 2));

    game_pool->games_size++;
    ensure_capacity(game_pool);
    game_pool->games[game_pool->games_size - 1] = game;
    return game;
}

Game *gp_find_game(GamePool *game_pool, int id) {
    for (int i = 0; i < game_pool->games_size; ++i) {
        Game* game = game_pool->games[i];
        if (game->id == id)
            return game;
    }
    return NULL;
}

bool gp_can_join_game(GamePool* game_pool, Session *session) {
    if (!session_is_logged(session))
        return false;

    if (session_is_in_game(session))
        return false;

    return true;
}

Game* gp_join_game(GamePool* game_pool, Session *session, int game_id) {
    Game* game = gp_find_game(game_pool, game_id);
    if (game != NULL) {
        session->game = game;
    }

    return game;
}

void gp_free(GamePool* game_pool) {
    if (game_pool->games_size > 0) {
        int i;

        for (i = 0; i < game_pool->games_size; i++) {
            Game *entry = game_pool->games[i];

            game_free(entry);
            free(entry);
        }

        free(game_pool->games);
    }

    game_pool->games = NULL;
    game_pool->games_size = 0;
    game_pool->games_capacity = 0;
    game_pool->last_id = -1;
}



/**
 * Contains structure for storing data about one game and its functions.
 *
 * @author: Patrik Harag
 * @version: 2017-10-29
 */

#include <stdlib.h>
#include "game.h"

void game_init(Game* game, int id, unsigned int w, unsigned int h) {
    game->id = id;
    game->w = w;
    game->h = h;
    game->finished = false;

    game->pieces = (Piece**) calloc(w * h, sizeof(Piece*));
    for (int i = 0; i < w * h; ++i) {
        Piece* p = (Piece *) calloc(1, sizeof(Piece));
        p->id = i;
        p->x = 0;
        p->y = 0;
        game->pieces[i] = p;
    }
}

void game_free(Game* game) {
    for (int i = 0; i < game->w * game->h; ++i) {
        free(game->pieces[i]);
    }
}

void game_shuffle(Game* game, int max_w, int max_h) {
    unsigned int last;

    for (int i = 0; i < game->w * game->h; ++i) {
        Piece* p = game->pieces[i];

        last = (unsigned int) (rand() % (max_w * 2));
        p->x = last - max_w;

        last = (unsigned int) (rand() % (max_h * 2));
        p->y = last - max_h;
    }
}

void game_check_is_finished(Game* game) {
    unsigned int h = game->h;
    unsigned int w = game->w;

    Piece* topLeft = game->pieces[0];

    for (int y = 0; y < h; ++y) {
        for (int x = 0; x < w; ++x) {
            int i = x + (y * w);
            Piece* p = game->pieces[i];

            if (abs(topLeft->x + (x * GAME_PIECE_SIZE) - p->x) > GAME_TOLERANCE
                    || abs(topLeft->y + (y * GAME_PIECE_SIZE) - p->y) > GAME_TOLERANCE) {

                return;
            }
        }
    }

    game->finished = true;
}

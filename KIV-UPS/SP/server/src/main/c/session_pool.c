
/**
 * Manages all sessions at one place.
 *
 * @author: Patrik Harag
 * @version: 2017-10-28
 */

#include <string.h>
#include "session_pool.h"

static void ensure_capacity(SessionPool* session_pool) {
    if (session_pool->sessions_capacity >= session_pool->sessions_size + 1)
        return;  // not needed

    if (session_pool->sessions == NULL) {
        session_pool->sessions_capacity = SESSION_POOL_DEFAULT_CAPACITY;
        session_pool->sessions = (Session **) calloc(session_pool->sessions_capacity, sizeof(Session*));
    } else  {
        session_pool->sessions_capacity *= SESSION_POOL_INCREASE_RATIO;
        session_pool->sessions = (Session **) realloc(
                session_pool->sessions, session_pool->sessions_capacity * sizeof(Session*));
    }
}

void sp_init(SessionPool* session_pool) {
    sp_free(session_pool);
}

bool sp_is_free_name(SessionPool* session_pool, char* name) {
    for (int i = 0; i < session_pool->sessions_size; ++i) {
        Session* session = session_pool->sessions[i];
        if (session->status == SESSION_STATUS_CONNECTED) {
            if (strcmp(session->name, name) == 0) {
                return false;
            }
        }
    }
    return true;
}

Session* sp_create(SessionPool* session_pool) {
    Session* session = (Session *) calloc(1, sizeof(Session));
    session_init(session);
    session->id = (int) session_pool->sessions_size;

    session_pool->sessions_size++;
    ensure_capacity(session_pool);
    session_pool->sessions[session->id] = session;
    return session;
}

void sp_free(SessionPool* session_pool) {
    if (session_pool->sessions_size > 0) {
        int i;

        for (i = 0; i < session_pool->sessions_size; i++) {
            Session *entry = session_pool->sessions[i];

            session_free(entry);
            free(entry);
        }

        free(session_pool->sessions);
    }

    session_pool->sessions = NULL;
    session_pool->sessions_size = 0;
    session_pool->sessions_capacity = 0;
}

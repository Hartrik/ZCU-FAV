
/**
 * Manages statistics.
 *
 * @author: Patrik Harag
 * @version: 2017-10-16
 */

#include <stdio.h>
#include <pthread.h>
#include <time.h>
#include "stats.h"
#include "utils.h"

static Stats stats;
static pthread_mutex_t stats_lock;

void stats_init() {
    if (pthread_mutex_init(&stats_lock, NULL) != 0) {
        perror("Mutex init failed");
    }

    stats.start = utils_current_millis();
    stats.bytes_sent = 0;
    stats.bytes_received = 0;
    stats.messages_sent = 0;
    stats.messages_received = 0;
    stats.connections_established = 0;
}

void stats_add_bytes_sent(long long bytes) {
    pthread_mutex_lock(&stats_lock);
    stats.bytes_sent += bytes;
    pthread_mutex_unlock(&stats_lock);
}

void stats_add_bytes_received(long long bytes) {
    pthread_mutex_lock(&stats_lock);
    stats.bytes_received += bytes;
    pthread_mutex_unlock(&stats_lock);
}

void stats_add_messages_sent(int messages) {
    pthread_mutex_lock(&stats_lock);
    stats.messages_sent += messages;
    pthread_mutex_unlock(&stats_lock);
}

void stats_add_messages_received(int messages) {
    pthread_mutex_lock(&stats_lock);
    stats.messages_received += messages;
    pthread_mutex_unlock(&stats_lock);
}

void stats_add_connections_established(int connections) {
    pthread_mutex_lock(&stats_lock);
    stats.connections_established += connections;
    pthread_mutex_unlock(&stats_lock);
}

void stats_store() {
    pthread_mutex_lock(&stats_lock);

    stats.end = utils_current_millis();

    FILE *f = fopen(STATS_FILE, "w");
    if (f == NULL) {
        perror("Error while creating stats file");
        return;

    } else {

        time_t t = time(NULL);
        struct tm tm = *localtime(&t);

        fprintf(f, "created: %d-%d-%d %d:%d:%d\n\n",
                tm.tm_year + 1900, tm.tm_mon + 1, tm.tm_mday,
                tm.tm_hour, tm.tm_min, tm.tm_sec);

        fprintf(f, "server_up: %llu [ms]\n", stats.end - stats.start);
        fprintf(f, "bytes_sent: %lld\n", stats.bytes_sent);
        fprintf(f, "bytes_received: %lld\n", stats.bytes_received);
        fprintf(f, "messages_sent: %d\n", stats.messages_sent);
        fprintf(f, "messages_received: %d\n", stats.messages_received);
        fprintf(f, "connections_established: %d\n", stats.connections_established);

        fclose(f);

        printf("Created '%s'\n", STATS_FILE);
    }

    pthread_mutex_unlock(&stats_lock);
}

void stats_free() {
    pthread_mutex_destroy(&stats_lock);
}

/**
 * Manages statistics.
 *
 * @author: Patrik Harag
 * @version: 2017-10-16
 */

#ifndef SERVER_STATS_H
#define SERVER_STATS_H

#define STATS_FILE "stats.txt"

typedef struct _Stats {
    unsigned long long start;
    unsigned long long end;

    long long bytes_sent;
    long long bytes_received;

    int messages_sent;
    int messages_received;

    int connections_established;
} Stats;

void stats_init();

void stats_add_bytes_sent(long long bytes);
void stats_add_bytes_received(long long bytes);

void stats_add_messages_sent(int messages);
void stats_add_messages_received(int messages);

void stats_add_connections_established(int connections);

void stats_store();

void stats_free();

#endif

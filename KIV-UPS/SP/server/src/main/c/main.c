
/**
 * Contains main method, parses parameters.
 *
 * @author: Patrik Harag
 * @version: 2017-10-31
 */

#include <stdio.h>
#include <signal.h>
#include <string.h>
#include "server.h"
#include "shared.h"
#include "stats.h"

#define PORT_MAX 65535
#define PORT_MIN 0
#define PORT_DEFAULT 8076


/**
 * Handles SIGINT signal.
 *
 * @param sig_number
 */
static void sigint_handler(int sig_number) {
    if (TERMINATED) return;

    printf("\nTERMINATED\n");
    TERMINATED = true;
}

/**
 * Initialize SIGINT signal handler.
 */
static void init_SIGINT_handler() {
    struct sigaction old_action;
    struct sigaction sa;
    memset(&sa, 0, sizeof(sa));
    sa.sa_handler = &sigint_handler;
    sigaction(SIGINT, &sa, &old_action);
}

/**
 * Returns port.
 *
 * @param argc program's argc
 * @param argv program's argv
 */
static int get_port(int argc, char* argv[]) {
    if (argc == 1)
        return PORT_DEFAULT;

    char *rest;
    long number = strtol(argv[1], &rest, 10);

    if (strlen(rest) == 0 && number >= PORT_MIN && number <= PORT_MAX) {
        return (int) number;
    } else {
        printf("Given port is not valid\n");
        exit(1);
    }
}

int main(int argc, char* argv[]) {
    int port = get_port(argc, argv);

    init_SIGINT_handler();

    stats_init();

    shared_init();
    shared_load();

    int ret = server_start(port);

    shared_store();
    shared_free();

    stats_store();
    stats_free();

    return ret;
}

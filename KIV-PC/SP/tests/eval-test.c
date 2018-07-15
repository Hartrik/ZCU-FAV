/*
 * File:   eval-test.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-13
 */

#include <stdio.h>
#include <stdlib.h>

#include "src/commons.h"
#include "src/types.h"
#include "src/interpreter.h"
#include "src/repl.h"

#define ERROR -1

/*
 * Simple C Test Suite
 */

int eval_as_int(char *input) {
    return repl_eval_as_int(input, ERROR);
}

int exception_propagation() {
    int passed = 1;

    passed &= eval_as_int("(list (list (list (/ 42 0))))") == ERROR;

    return passed;
}

int syntax_errors() {
    int passed = 1;

    passed &= eval_as_int("(") == ERROR;
    passed &= eval_as_int(")") == ERROR;
    passed &= eval_as_int("((") == ERROR;
    passed &= eval_as_int("(+ 1 2 ()") == ERROR;
    passed &= eval_as_int(")+ 1 2 (") == ERROR;
    passed &= eval_as_int("(+ (+ 1 2") == ERROR;

    return passed;
}

int signed_numbers() {
    int passed = 1;

    passed &= eval_as_int("-1") == -1;
    passed &= eval_as_int("-42") == -42;
    passed &= eval_as_int("-4a4") == ERROR;

    passed &= eval_as_int("+1") == +1;
    passed &= eval_as_int("+42") == +42;
    passed &= eval_as_int("+4a4") == ERROR;

    return passed;
}

int main(int argc, char** argv) {
    interpreter_init();

    printf("%%SUITE_STARTING%% evaluation\n");
    printf("%%SUITE_STARTED%%\n");

    {
        printf("%%TEST_STARTED%% exception_propagation (evaluation)\n");
        if (exception_propagation() == 0)
            printf("%%TEST_FAILED%% time=0 testname=exception_propagation (evaluation) message=error\n");
        printf("%%TEST_FINISHED%% time=0 exception_propagation (evaluation) \n");

        printf("%%TEST_STARTED%% syntax_errors (evaluation)\n");
        if (syntax_errors() == 0)
            printf("%%TEST_FAILED%% time=0 testname=syntax_errors (evaluation) message=error\n");
        printf("%%TEST_FINISHED%% time=0 syntax_errors (evaluation) \n");

        printf("%%TEST_STARTED%% signed_numbers (evaluation)\n");
        if (signed_numbers() == 0)
            printf("%%TEST_FAILED%% time=0 testname=signed_numbers (evaluation) message=error\n");
        printf("%%TEST_FINISHED%% time=0 signed_numbers (evaluation) \n");
    }

    printf("%%SUITE_FINISHED%% time=0\n");

    interpreter_shutdown();
    return (EXIT_SUCCESS);
}

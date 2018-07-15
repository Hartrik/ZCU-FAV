/*
 * File:   built-in-test.c
 * Author: Patrik Harag
 *
 * Created: 2016-11-12
 */

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

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

int eval_as_logical(char *input) {
    return repl_eval_as_logical(input, ERROR);
}

int arithmetic_functions() {
    int passed = 1;

    /* scitani */
    passed &= eval_as_int("(+)") == 0;
    passed &= eval_as_int("(+ 1)") == 1;
    passed &= eval_as_int("(+ 1 2)") == 3;
    passed &= eval_as_int("(+ 1 2 3)") == 6;

    /* odcitani */
    passed &= eval_as_int("(-)") == ERROR;
    passed &= eval_as_int("(- 10)") == -10;
    passed &= eval_as_int("(- 10 1)") == 9;
    passed &= eval_as_int("(- 10 1 2)") == 7;

    /* nasobeni */
    passed &= eval_as_int("(*)") == 1;
    passed &= eval_as_int("(* 1)") == 1;
    passed &= eval_as_int("(* 1 2)") == 2;
    passed &= eval_as_int("(* 1 2 3)") == 6;

    /* deleni */
    passed &= eval_as_int("(/)") == ERROR;
    passed &= eval_as_int("(/ 100)") == ERROR;
    passed &= eval_as_int("(/ 100 4)") == 25;
    passed &= eval_as_int("(/ 100 4 5)") == 5;
    passed &= eval_as_int("(/ 100 0)") == ERROR;

    return passed;
}

int compare_functions() {
    int passed = 1;

    /* rovnost */
    passed &= eval_as_logical("(=)") == true;
    passed &= eval_as_logical("(= 1)") == true;
    passed &= eval_as_logical("(= 1 1)") == true;
    passed &= eval_as_logical("(= 1 2)") == false;
    passed &= eval_as_logical("(= 1 2 3)") == false;

    /* nerovnost */
    passed &= eval_as_logical("(/=)") == true;
    passed &= eval_as_logical("(/= 1)") == true;
    passed &= eval_as_logical("(/= 1 1)") == false;
    passed &= eval_as_logical("(/= 1 2)") == true;
    passed &= eval_as_logical("(/= 1 2 3)") == true;

    /* mensi nez */
    passed &= eval_as_logical("(<)") == true;
    passed &= eval_as_logical("(< 1)") == true;
    passed &= eval_as_logical("(< 1 2)") == true;
    passed &= eval_as_logical("(< 1 2 3)") == true;
    passed &= eval_as_logical("(< 1 0)") == false;
    passed &= eval_as_logical("(< 1 2 1)") == false;
    passed &= eval_as_logical("(< 1 1)") == false;

    /* vetsi nez */
    passed &= eval_as_logical("(>)") == true;
    passed &= eval_as_logical("(> 1)") == true;
    passed &= eval_as_logical("(> 3 2)") == true;
    passed &= eval_as_logical("(> 3 2 1)") == true;
    passed &= eval_as_logical("(> 1 2 )") == false;
    passed &= eval_as_logical("(> 3 2 3)") == false;
    passed &= eval_as_logical("(> 1 1)") == false;

    /* mensi nez nebo rovno */
    passed &= eval_as_logical("(<=)") == true;
    passed &= eval_as_logical("(<= 1)") == true;
    passed &= eval_as_logical("(<= 1 2)") == true;
    passed &= eval_as_logical("(<= 1 2 3)") == true;
    passed &= eval_as_logical("(<= 1 0)") == false;
    passed &= eval_as_logical("(<= 1 2 1)") == false;
    passed &= eval_as_logical("(<= 1 1)") == true;

    /* vetsi nez nebo rovno */
    passed &= eval_as_logical("(>=)") == true;
    passed &= eval_as_logical("(>= 1)") == true;
    passed &= eval_as_logical("(>= 3 2)") == true;
    passed &= eval_as_logical("(>= 3 2 1)") == true;
    passed &= eval_as_logical("(>= 1 2 )") == false;
    passed &= eval_as_logical("(>= 3 2 3)") == false;
    passed &= eval_as_logical("(>= 1 1)") == true;

    return passed;
}

int set_function() {
    int passed = 1;

    /* chybne vstupy */
    passed &= eval_as_int("(set)") == ERROR;
    passed &= eval_as_int("(set 'a)") == ERROR;
    passed &= eval_as_int("(set 1 3)") == ERROR;
    passed &= eval_as_int("(set () 3)") == ERROR;
    passed &= eval_as_int("(set T 3)") == ERROR;    /* konstanta */
    passed &= eval_as_int("(set NIL 3)") == ERROR;  /* konstanta */

    /* pouziti */
    passed &= eval_as_int("(set 'a 3)") == 3;
    passed &= eval_as_int("(set 'b 5)") == 5;
    passed &= eval_as_int("a") == 3;
    passed &= eval_as_int("b") == 5;
    passed &= eval_as_int("(+ a b)") == 8;

    /* zmena */
    passed &= eval_as_int("(set 'a 13)") == 13;
    passed &= eval_as_int("(set 'b 15)") == 15;
    passed &= eval_as_int("a") == 13;
    passed &= eval_as_int("b") == 15;
    passed &= eval_as_int("(+ a b)") == 28;

    return passed;
}

int list_functions() {
    int passed = 1;

    /* length */
    passed &= eval_as_int("(length 5)") == ERROR;
    passed &= eval_as_int("(length (list))") == 0;
    passed &= eval_as_int("(length (list 1))") == 1;
    passed &= eval_as_int("(length (list 1 2))") == 2;

    /* elt */
    passed &= eval_as_int("(elt 5)") == ERROR;
    passed &= eval_as_int("(elt 5 1)") == ERROR;
    passed &= eval_as_int("(elt (list 1))") == ERROR;
    passed &= eval_as_int("(elt (list 1) 1)") == ERROR;
    passed &= eval_as_int("(elt (list 1) (- 1))") == ERROR;
    passed &= eval_as_int("(elt (list 1) 0)") == 1;
    passed &= eval_as_int("(elt (list 1 2 3) 1)") == 2;

    /* car */
    passed &= eval_as_logical("(car 5)") == ERROR;
    passed &= eval_as_logical("(car (list))") == false;
    passed &= eval_as_int("(car (list 1))") == 1;
    passed &= eval_as_int("(car (list 1 2))") == 1;

    /* cdr */
    passed &= eval_as_logical("(cdr 5)") == ERROR;
    passed &= eval_as_logical("(cdr (list))") == false;
    passed &= eval_as_int("(car (cdr (list 1 2)))") == 2;

    /* conj */
    passed &= eval_as_int("(conj)") == ERROR;
    passed &= eval_as_int("(conj 1)") == ERROR;
    passed &= eval_as_int("(conj 1 1)") == ERROR;
    passed &= eval_as_int("(car (conj 5 (list)))") == 5;
    passed &= eval_as_int("(car (conj 5 (list 4)))") == 4;
    passed &= eval_as_int("(car (conj 5 (list 4 4)))") == 4;

    /* cons */
    passed &= eval_as_int("(cons)") == ERROR;
    passed &= eval_as_int("(cons 1)") == ERROR;
    passed &= eval_as_int("(cons 1 1)") == ERROR;
    passed &= eval_as_int("(car (cons 5 (list)))") == 5;
    passed &= eval_as_int("(car (cons 5 (list 4)))") == 5;
    passed &= eval_as_int("(car (cons 5 (list 4 4)))") == 5;

    /* reduce */
    passed &= eval_as_int("(reduce)") == ERROR;
    passed &= eval_as_int("(reduce 1)") == ERROR;
    passed &= eval_as_int("(reduce 1 1)") == ERROR;
    passed &= eval_as_int("(reduce 1 1 1)") == ERROR;
    passed &= eval_as_int("(reduce + 0 (list))") == 0;
    passed &= eval_as_int("(reduce + 1 (list 2 3))") == 6;
    passed &= eval_as_int("(reduce * 1 (list 1 2 3 4))") == 24;

    return passed;
}

int if_form() {
    int passed = 1;

    passed &= eval_as_int("(if)") == ERROR;
    passed &= eval_as_int("(if T)") == ERROR;
    passed &= eval_as_int("(if T 1)") == ERROR;
    passed &= eval_as_int("(if 'not_logical 1 2)") == ERROR;
    passed &= eval_as_int("(if NIL 1 2 3") == ERROR;
    passed &= eval_as_int("(if T 1 2)") == 1;
    passed &= eval_as_int("(if NIL 1 2)") == 2;

    return passed;
}

int main(int argc, char** argv) {
    interpreter_init();

    printf("%%SUITE_STARTING%% built-in\n");
    printf("%%SUITE_STARTED%%\n");

    {
        printf("%%TEST_STARTED%% arithmetic_functions (built-in)\n");
        if (arithmetic_functions() == 0)
            printf("%%TEST_FAILED%% time=0 testname=arithmetic_functions (built-in) message=error\n");
        printf("%%TEST_FINISHED%% time=0 arithmetic_functions (built-in) \n");

        printf("%%TEST_STARTED%% compare_functions (built-in)\n");
        if (compare_functions() == 0)
            printf("%%TEST_FAILED%% time=0 testname=compare_functions (built-in) message=error\n");
        printf("%%TEST_FINISHED%% time=0 compare_functions (built-in) \n");

        printf("%%TEST_STARTED%% set_function (built-in)\n");
        if (set_function() == 0)
            printf("%%TEST_FAILED%% time=0 testname=set_function (built-in) message=error\n");
        printf("%%TEST_FINISHED%% time=0 set_function (built-in) \n");

        printf("%%TEST_STARTED%% list_functions (built-in)\n");
        if (list_functions() == 0)
            printf("%%TEST_FAILED%% time=0 testname=list_functions (built-in) message=error\n");
        printf("%%TEST_FINISHED%% time=0 list_functions (built-in) \n");

        printf("%%TEST_STARTED%% if_form (built-in)\n");
        if (if_form() == 0)
            printf("%%TEST_FAILED%% time=0 testname=if_form (built-in) message=error\n");
        printf("%%TEST_FINISHED%% time=0 if_form (built-in) \n");
    }

    printf("%%SUITE_FINISHED%% time=0\n");

    interpreter_shutdown();
    return (EXIT_SUCCESS);
}

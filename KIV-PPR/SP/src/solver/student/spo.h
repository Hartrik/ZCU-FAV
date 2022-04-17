#pragma once

#include "..\..\common\iface\SolverIface.h"

void free_2d_matrix(double **matrix, size_t rows);

double ** generate_search_points(solver::TSolver_Setup &setup);

double ** create_matrix_S(size_t n);
double ** create_matrix_S_minus_I(size_t n, double **S);
double * multiply_vect(
    double **a, size_t am, size_t an,
    double *b,
    double *r);

double count_fitness(solver::TSolver_Setup &setup, double* point);
void collect_fitness(solver::TSolver_Setup &setup, double *fitness_results,
    double &min_fitness, int &min_fitness_index, double &sum_fitness);

#include "..\solver_smp.h"
#include "spo.h"
#include <random>
#include <limits>
#include <math.h>
#include <iostream>

static const int MAX_ITER_WITHOUT_IMPROVEMENT = 256;

// Spocte fitness pro vsechny body
void calculate_fitness_serial(solver::TSolver_Setup &setup, double **search_points, double *fitness_results) {
    for (int i = 0; i < setup.population_size; ++i) {
        fitness_results[i] = count_fitness(setup, search_points[i]);
    }
}

HRESULT solve_serial(solver::TSolver_Setup &setup, solver::TSolver_Progress &progress) {
    double min_fitness;
    int min_fitness_index;
    double min_sum_fitness = std::numeric_limits<double>::max();
    int min_sum_fitness_k = 0;
    double *fitness = new double[setup.population_size];

    double **search_points = generate_search_points(setup);
    calculate_fitness_serial(setup, search_points, fitness);
    collect_fitness(setup, fitness, min_fitness, min_fitness_index, min_sum_fitness);

    double **S = create_matrix_S(setup.problem_size);  // S
    double **SI = create_matrix_S_minus_I(setup.problem_size, S);  // S-I
    double *SIc = nullptr;  // (S-I)*c
    double *Sx = nullptr;  // S*x

    int k;
    for (k = 1; k < setup.max_generations; k++) {
        // otoceni podle centroidu c - ten s nejmensi fitness
        // x = S*x - (S-I)*c

        double *c_vec = search_points[min_fitness_index];
        SIc = multiply_vect(SI, setup.problem_size, setup.problem_size, c_vec, SIc);
        
        for (int i = 0; i < setup.population_size; ++i) {
            // center preskocime
            if (i == min_fitness_index) continue;

            double *x_vec = search_points[i];
            Sx = multiply_vect(S, setup.problem_size, setup.problem_size, x_vec, Sx);
            for (int j = 0; j < setup.problem_size; j++) {
                x_vec[j] = Sx[j] - SIc[j];
            }

            // vypocet fitness presunuteho bodu
            fitness[i] = count_fitness(setup, x_vec);
        }

        // vypocet nove fitness
        double sum_fitness;
        collect_fitness(setup, fitness, min_fitness, min_fitness_index, sum_fitness);

        // kontrola zastaveni
        if (sum_fitness < min_sum_fitness) {
            min_sum_fitness_k = k;
            min_sum_fitness = sum_fitness;
        }
        else if (k - min_sum_fitness_k > MAX_ITER_WITHOUT_IMPROVEMENT) {
            break;
        }
    }

    // zapsani vysledku
    progress.best_metric = min_fitness;
    for (int i = 0; i < setup.problem_size; i++) {
        setup.solution[i] = search_points[min_fitness_index][i];
    }

    // uklid
    delete[] SIc;
    delete[] Sx;
    delete[] fitness;
    free_2d_matrix(search_points, setup.population_size);
    free_2d_matrix(S, setup.problem_size);
    free_2d_matrix(SI, setup.problem_size);

    return S_OK;
}
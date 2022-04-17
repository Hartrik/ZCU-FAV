#include "spo.h"

#include <random>
#include <limits>
#include <math.h>
#include <iostream>
#include <tbb/tbb.h>

static const double R = 0.95;
static const double THETA = 3.14159265358979323846 / 4;

static const double BOUND_OFFSET = 0.10;

// Vygeneruje search point uprostred prostoru
size_t generate_add_center(solver::TSolver_Setup &setup, double **search_points, size_t offset) {
    if (offset < setup.population_size) {
        double* center = search_points[offset];
        for (int j = 0; j < setup.problem_size; j++) {
            const double val = (setup.upper_bound[j] - setup.lower_bound[j]) / 2;
            center[j] = val;
        }
        return 1;
    }
    else {
        return 0;
    }
}

// Vygeneruje search points do vsech "rohu" prostoru
size_t generate_add_corners(solver::TSolver_Setup &setup, double **search_points, size_t offset) {
    size_t left = setup.population_size - offset;
    size_t corner_points = std::min<size_t>((size_t)std::pow(2l, setup.problem_size), left);
    for (int i = 0; i < corner_points; i++) {
        double* point = search_points[offset + i];
        for (int j = 0; j < setup.problem_size; j++) {
            const double val = (i & (1 << j))
                ? setup.lower_bound[j] + BOUND_OFFSET * (setup.upper_bound[j] - setup.lower_bound[j])
                : setup.upper_bound[j] - BOUND_OFFSET * (setup.upper_bound[j] - setup.lower_bound[j]);
            point[j] = val;
        }
    }
    return corner_points;
}

// Vygeneruje nahodne search points
size_t generate_add_random(solver::TSolver_Setup &setup, double **search_points, size_t offset) {
    std::mt19937 generator(23);
    for (int j = 0; j < setup.problem_size; j++) {
        std::uniform_real_distribution<double> random(setup.lower_bound[j], setup.upper_bound[j]);
        for (size_t i = offset; i < setup.population_size; ++i) {
            const double rnd = random(generator);
            search_points[i][j] = rnd;
        }
    }
    return setup.population_size - offset;
}

// Vygeneruje search points
double ** generate_search_points(solver::TSolver_Setup &setup) {
    // initialize array
    double **search_points = new double*[setup.population_size];
    for (int i = 0; i < setup.population_size; ++i) {
        double* point = new double[setup.problem_size];
        search_points[i] = point;
    }

    size_t generated = 0;
    generated += generate_add_center(setup, search_points, generated);
    generated += generate_add_corners(setup, search_points, generated);
    generate_add_random(setup, search_points, generated);
    return search_points;
}

// Vynasobi dve matice
double ** multiply(
    double **a, size_t am, size_t an,
    double **b, size_t bm, size_t bn,
    double **r) {

    // rm = am
    // rn = bn

    if (r == nullptr) {
        // vytvoreni nove matice
        r = new double*[am];
        for (int i = 0; i < am; ++i) {
            double *row = new double[bn];
            for (int j = 0; j < bn; j++) {
                row[j] = 0;
            }
            r[i] = row;
        }
    }
    else {
        // vynulovani matice s vysledkem
        for (int i = 0; i < am; ++i) {
            for (int j = 0; j < bn; j++) {
                r[i][j] = 0;
            }
        }
    }

    for (int i = 0; i < am; ++i) {
        for (int j = 0; j < bn; ++j) {
            for (int k = 0; k < bm; ++k) {
                r[i][j] += a[i][k] * b[k][j];
            }
        }
    }

    return r;
}

// Vynasobi matici se sloupcovym vektorem, vysledek je opet sloupcovy vektor.
double * multiply_vect(
    double **a, size_t am, size_t an,
    double *b,
    double *r) {

    // an = take velikost vektoru

    if (r == nullptr) {
        // vytvoreni noveho vektoru
        r = new double[an];
    }

    for (int i = 0; i < am; ++i) {
        double v = 0;
        for (int j = 0; j < an; ++j) {
            v += a[i][j] * b[j];
        }
        r[i] = v;
    }

    return r;
}

// Vynasobi matici skalarem
void multiply(double** a, size_t am, size_t an, double number) {
    for (int i = 0; i < am; ++i) {
        for (int j = 0; j < an; ++j) {
            a[i][j] *= number;
        }
    }
}

// Uvolni matici
void free_2d_matrix(double **matrix, size_t rows) {
    for (int i = 0; i < rows; ++i) {
        delete[] matrix[i];
    }
    delete[] matrix;
}

// Vytvori matici R
double ** create_matrix_R(size_t n, int i, int j) {
    i = i - 1;
    j = j - 1;

    double **r = new double*[n];
    for (int idx_i = 0; idx_i < n; ++idx_i) {
        double *row = new double[n];
        for (int idx_j = 0; idx_j < n; idx_j++) {
            if (idx_i == i && idx_j == i) {
                row[idx_j] = std::cos(THETA);
            }
            else if (idx_i == i && idx_j == j) {
                row[idx_j] = -std::sin(THETA);
            }
            else if (idx_i == j && idx_j == i) {
                row[idx_j] = std::sin(THETA);
            }
            else if (idx_i == j && idx_j == j) {
                row[idx_j] = std::cos(THETA);
            }
            else if (idx_i == idx_j) {
                row[idx_j] = 1;
            }
            else {
                row[idx_j] = 0;
            }
        }
        r[idx_i] = row;
    }
    return r;
}

// Vytvori matici S
double ** create_matrix_S(size_t n) {
    double **r = new double*[n];
    // inicializace matice jako jednotkove
    for (int i = 0; i < n; ++i) {
        double *row = new double[n];
        for (int j = 0; j < n; j++) {
            row[j] = (i == j) ? 1 : 0;
        }
        r[i] = row;
    }

    for (int i = 1; i < n; i++) {
        for (int j = i + 1; j <= n; j++) {
            double **tmp = create_matrix_R(n, i, j);
            double **mult = multiply(r, n, n, tmp, n, n, nullptr);
            free_2d_matrix(r, n);
            free_2d_matrix(tmp, n);
            r = mult;
        }
    }

    multiply(r, n, n, R);
    return r;
}

// Vytvori matici S-I
double ** create_matrix_S_minus_I(size_t n, double **S) {
    double **SI = new double*[n];
    for (int i = 0; i < n; ++i) {
        SI[i] = new double[n];
        for (int j = 0; j < n; j++) {
            SI[i][j] = S[i][j] - (i == j ? 1 : 0);
        }
    }
    return SI;
}

// Spocte fitness pro jeden bod
double count_fitness(solver::TSolver_Setup &setup, double* point) {
    // muze se realne stat, ze nenizsi hodnotu fitness bude mit bod mimo rozsah
    // a my nechceme aby tento bod "unesl" ostatni
    // timto ho vyradime z vyberu
    for (int j = 0; j < setup.problem_size; j++) {
        if (point[j] < setup.lower_bound[j] || point[j] > setup.upper_bound[j]) {
            return std::numeric_limits<double>::max();
        }
    }
    return setup.objective(setup.data, point);
}

// Projde fitness hodnoty a urci minimum a sumu
void collect_fitness(solver::TSolver_Setup &setup, double *fitness_results,
    double &min_fitness, int &min_fitness_index, double &sum_fitness) {

    min_fitness = std::numeric_limits<double>::max();
    min_fitness_index = -1;
    sum_fitness = 0;

    for (int i = 0; i < setup.population_size; ++i) {
        double fitness = fitness_results[i];
        if (fitness < min_fitness) {
            min_fitness = fitness;
            min_fitness_index = i;
        }
        if (fitness != std::numeric_limits<double>::max()) {
            sum_fitness += fitness;
        }
    }
}

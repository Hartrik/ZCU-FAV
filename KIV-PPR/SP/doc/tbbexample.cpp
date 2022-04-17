void calculate_fitness_smp(solver::TSolver_Setup &setup, double **search_points, double *fitness_results) {
    tbb::parallel_for(tbb::blocked_range<int>(0, (int)setup.population_size), [&](tbb::blocked_range<int> r) {
        for (int i = r.begin(); i < r.end(); ++i) {
            fitness_results[i] = count_fitness(setup, search_points[i]);
        }
    });
}
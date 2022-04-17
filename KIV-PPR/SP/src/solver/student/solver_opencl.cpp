#include "..\solver_opencl.h"
#include "spo.h"
#include <random>
#include <limits>
#include <math.h>
#include <iostream>

#define CL_USE_DEPRECATED_OPENCL_1_2_APIS
#include <CL/cl.hpp>

//#define DEBUG_DEVICES

static const int MAX_ITER_WITHOUT_IMPROVEMENT = 256;

// Spocte fitness pro vsechny body
void calculate_fitness(solver::TSolver_Setup &setup, double **search_points, double *fitness_results) {
    for (int i = 0; i < setup.population_size; ++i) {
        fitness_results[i] = count_fitness(setup, search_points[i]);
    }
}

// Pomocna struktura pro udrzeni kontextu OpenCL
struct opencl_context {
    cl_uint num_platforms;
    cl_platform_id *platforms;

    cl_uint num_devices;
    cl_device_id *devices;

    cl_context context;
    cl_command_queue cmd_queue;
} _opencl_context;

// Vypise informace o zarizenich (Debug)
void display_device_details(opencl_context *opencl) {
    printf("Devices: %d\n", opencl->num_devices);
    cl_int status;

    for (size_t i = 0; i < opencl->num_devices; i++) {
        const size_t buffer_size = 200;
        char data[buffer_size];
        size_t written = 0;

        printf("--------\n");
        status = clGetDeviceInfo(opencl->devices[i], CL_DEVICE_NAME, 200, data, &written);
        printf("CL_DEVICE_NAME (%zd): %s\n", written, data);
        written = 0;
        data[0] = 0;
        
        status = clGetDeviceInfo(opencl->devices[i], CL_DEVICE_VERSION, 200, data, &written);
        printf("CL_DEVICE_VERSION (%zd): %s\n", written, data);
        written = 0;
        data[0] = 0;

        status = clGetDeviceInfo(opencl->devices[i], CL_DEVICE_EXTENSIONS, 200, data, &written);
        printf("CL_DEVICE_EXTENSIONS (%zd): %s\n", written, data);
    }
}

// Inicializuje OpenCL a vrati pointer na strukturu s kontextem
opencl_context * init_opencl(cl_int &status) {
    opencl_context *opencl = new opencl_context();
    opencl->num_platforms = 0;

    // nalezeni a inicializace platform
    status = clGetPlatformIDs(0, NULL, &opencl->num_platforms);
    opencl->platforms = new cl_platform_id[opencl->num_platforms];
    status = clGetPlatformIDs(opencl->num_platforms, opencl->platforms, NULL);

    // nalezeni a inicializace zarizeni
    opencl->num_devices = 0;
    status = clGetDeviceIDs(opencl->platforms[0], CL_DEVICE_TYPE_ALL, 0, NULL, &opencl->num_devices);
    opencl->devices = new cl_device_id[opencl->num_devices];
    status = clGetDeviceIDs(opencl->platforms[0], CL_DEVICE_TYPE_ALL, opencl->num_devices, opencl->devices, NULL);

#ifdef DEBUG_DEVICES
    display_device_details(opencl);
#endif

    int selected_device = 0;

    // vytvoreni kontextu a command queue
    opencl->context = clCreateContext(NULL, opencl->num_devices, opencl->devices, NULL, NULL, &status);
    opencl->cmd_queue = clCreateCommandQueue(opencl->context, opencl->devices[selected_device], 0, &status);

    return opencl;
}

const char *opencl_source = "                           \n\
        __kernel void multiply_vect(                    \n\
                __global float *A,                      \n\
                __global float *B,                      \n\
                __global float *C,                      \n\
                const int num) {                        \n\
                                                        \n\
            int i = get_global_id(0);                   \n\
            float v = 0;                                \n\
            for (int j = 0; j < num; ++j) {             \n\
                v += A[i*num + j] * B[j];               \n\
            }                                           \n\
            C[i] = v;                                   \n\
        }                                               ";

// Zkopiruje 2D pole typu double do 1D pole typu float
void to_float_buffer_2d(double **from, float *to, size_t n) {
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            to[i*n + j] = (float)from[i][j];
        }
    }
}

// Zkopiruje pole typu double do pole typu float
void to_float_buffer(double *from, float *to, size_t n) {
    for (int i = 0; i < n; i++) {
        to[i] = (float)from[i];
    }
}

HRESULT solve_opencl(solver::TSolver_Setup &setup, solver::TSolver_Progress &progress) {
    double min_fitness;
    int min_fitness_index;
    double min_sum_fitness = std::numeric_limits<double>::max();
    int min_sum_fitness_k = 0;
    double *fitness = new double[setup.population_size];

    double **search_points = generate_search_points(setup);
    calculate_fitness(setup, search_points, fitness);
    collect_fitness(setup, fitness, min_fitness, min_fitness_index, min_sum_fitness);

    double **S_double = create_matrix_S(setup.problem_size);  // S (double)
    double **SI_double = create_matrix_S_minus_I(setup.problem_size, S_double);  // S-I (double)
    float *S = new float[setup.problem_size * setup.problem_size];  // S
    to_float_buffer_2d(S_double, S, setup.problem_size);
    float *SI = new float[setup.problem_size * setup.problem_size];  // S-I
    to_float_buffer_2d(SI_double, SI, setup.problem_size);

    float *SIc = new float[setup.problem_size];  // (S-I)*c
    float *Sx = new float[setup.problem_size];  // S*x

    float *temp = new float[setup.problem_size];

    // inicializace zarizeni

    cl_int status;
    opencl_context *opencl = init_opencl(status);

    // priprava programu
    cl_program program = clCreateProgramWithSource(opencl->context, 1, &opencl_source, NULL, &status);
    status = clBuildProgram(program, opencl->num_devices, opencl->devices, NULL, NULL, NULL);

    // vytvoreni bufferu
    size_t buffer_vec_size = sizeof(float) * setup.problem_size;
    size_t buffer_matrix_size = sizeof(float) * setup.problem_size * setup.problem_size;
    cl_mem buffer_A = clCreateBuffer(opencl->context, CL_MEM_READ_ONLY, buffer_matrix_size, NULL, &status);
    cl_mem buffer_B = clCreateBuffer(opencl->context, CL_MEM_READ_ONLY, buffer_vec_size, NULL, &status);
    cl_mem buffer_C = clCreateBuffer(opencl->context, CL_MEM_WRITE_ONLY, buffer_vec_size, NULL, &status);

    // vytvoreni kernelu a asociace bufferu
    cl_kernel kernel = clCreateKernel(program, "multiply_vect", &status);
    status = clSetKernelArg(kernel, 0, sizeof(cl_mem), &buffer_A);
    status |= clSetKernelArg(kernel, 1, sizeof(cl_mem), &buffer_B);
    status |= clSetKernelArg(kernel, 2, sizeof(cl_mem), &buffer_C);
    status |= clSetKernelArg(kernel, 3, sizeof(int), &setup.problem_size);
    size_t global_work_size[1] = { setup.problem_size };

    int k;
    for (k = 1; k < setup.max_generations; k++) {
        // otoceni podle centroidu c - ten s nejmensi fitness
        // x = S*x - (S-I)*c

        double *c_vec = search_points[min_fitness_index];
        
        status = clEnqueueWriteBuffer(opencl->cmd_queue, buffer_A, CL_FALSE, 0, buffer_matrix_size, SI, 0, NULL, NULL);
        to_float_buffer(c_vec, temp, setup.problem_size);
        status = clEnqueueWriteBuffer(opencl->cmd_queue, buffer_B, CL_FALSE, 0, buffer_vec_size, temp, 0, NULL, NULL);
        status = clEnqueueNDRangeKernel(opencl->cmd_queue, kernel, 1, NULL, global_work_size, NULL, 0, NULL, NULL);
        clEnqueueReadBuffer(opencl->cmd_queue, buffer_C, CL_TRUE, 0, buffer_vec_size, SIc, 0, NULL, NULL);

        status = clEnqueueWriteBuffer(opencl->cmd_queue, buffer_A, CL_FALSE, 0, buffer_matrix_size, S, 0, NULL, NULL);
        for (int i = 0; i < setup.population_size; ++i) {
            // center preskocime
            if (i == min_fitness_index) continue;

            double *x_vec = search_points[i];

            to_float_buffer(x_vec, temp, setup.problem_size);
            status = clEnqueueWriteBuffer(opencl->cmd_queue, buffer_B, CL_FALSE, 0, buffer_vec_size, temp, 0, NULL, NULL);
            status = clEnqueueNDRangeKernel(opencl->cmd_queue, kernel, 1, NULL, global_work_size, NULL, 0, NULL, NULL);
            clEnqueueReadBuffer(opencl->cmd_queue, buffer_C, CL_TRUE, 0, buffer_vec_size, Sx, 0, NULL, NULL);

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

    clFlush(opencl->cmd_queue);
    clFinish(opencl->cmd_queue);
    clReleaseKernel(kernel);
    clReleaseProgram(program);
    clReleaseCommandQueue(opencl->cmd_queue);
    clReleaseMemObject(buffer_A);
    clReleaseMemObject(buffer_B);
    clReleaseMemObject(buffer_C);
    clReleaseContext(opencl->context);

    delete[] opencl->platforms;
    delete[] opencl->devices;
    delete opencl;

    delete[] S;
    delete[] SI;
    delete[] SIc;
    delete[] Sx;
    delete[] temp;
    delete[] fitness;
    free_2d_matrix(search_points, setup.population_size);
    free_2d_matrix(S_double, setup.problem_size);
    free_2d_matrix(SI_double, setup.problem_size);

    return S_OK;
}
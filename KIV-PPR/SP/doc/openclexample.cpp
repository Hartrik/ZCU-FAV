
const char *opencl_source = "                           \
        __kernel void vec_subst(                        \
                __global int *A,                        \
                __global int *B,                        \
                __global int *C) {                      \
            int idx = get_global_id(0);                 \
            C[idx] = A[idx] - B[idx];                   \
        }                                               ";

void test() {
    // ---------- data

    const int elements = 12;

    int *A = new int[elements];  // in
    int *B = new int[elements];  // in
    int *C = new int[elements];  // out
    for (int i = 0; i < elements; i++) {
        A[i] = i + 1;
        B[i] = i;
    }

    // ---------- inicializace zarizeni

    cl_int status;

    // nalezeni a inicializace platform
    cl_uint num_platforms = 0;
    status = clGetPlatformIDs(0, NULL, &num_platforms);
    cl_platform_id *platforms = new cl_platform_id[num_platforms];
    status = clGetPlatformIDs(num_platforms, platforms, NULL);

    // nalezeni a inicializace zarizeni
    cl_uint num_devices = 0;
    status = clGetDeviceIDs(platforms[0], CL_DEVICE_TYPE_ALL, 0, NULL, &num_devices);
    cl_device_id *devices = new cl_device_id[num_devices];
    status = clGetDeviceIDs(platforms[0], CL_DEVICE_TYPE_ALL, num_devices, devices, NULL);

    // vytvoreni kontextu a command queue
    cl_context context = clCreateContext(NULL, num_devices, devices, NULL, NULL, &status);
    cl_command_queue cmd_queue = clCreateCommandQueue(context, devices[0], 0, &status);

    // ---------- inicializace kernelu

    // priprava programu
    cl_program program = clCreateProgramWithSource(context, 1, (const char**)&opencl_source, NULL, &status);
    status = clBuildProgram(program, num_devices, devices, NULL, NULL, NULL);

    // vytvoreni bufferu
    cl_mem buffer_A = clCreateBuffer(context, CL_MEM_READ_ONLY, sizeof(int) * elements, NULL, &status);
    cl_mem buffer_B = clCreateBuffer(context, CL_MEM_READ_ONLY, sizeof(int) * elements, NULL, &status);
    cl_mem buffer_C = clCreateBuffer(context, CL_MEM_WRITE_ONLY, sizeof(int) * elements, NULL, &status);

    // vytvoreni kernelu a asociace bufferu
    cl_kernel kernel = clCreateKernel(program, "vec_subst", &status);
    status = clSetKernelArg(kernel, 0, sizeof(cl_mem), &buffer_A);
    status |= clSetKernelArg(kernel, 1, sizeof(cl_mem), &buffer_B);
    status |= clSetKernelArg(kernel, 2, sizeof(cl_mem), &buffer_C);

    // ---------- vykonani

    status = clEnqueueWriteBuffer(cmd_queue, buffer_A, CL_FALSE, 0, sizeof(int) * elements, A, 0, NULL, NULL);
    status = clEnqueueWriteBuffer(cmd_queue, buffer_B, CL_FALSE, 0, sizeof(int) * elements, B, 0, NULL, NULL);

    size_t global_work_size[1] = { elements };
    status = clEnqueueNDRangeKernel(cmd_queue, kernel, 1, NULL, global_work_size, NULL, 0, NULL, NULL);

    clEnqueueReadBuffer(cmd_queue, buffer_C, CL_TRUE, 0, sizeof(int) * elements, C, 0, NULL, NULL);

    // ---------- kontrola vysledku

    bool result = true;
    for (int i = 0; i < elements; i++) {
        if (C[i] != 1) {
            result = false;
            break;
        }
    }
    printf(result ? "Output is correct\n" : "Output is incorrect\n");

    // ---------- uvolneni zdroju

    clReleaseKernel(kernel);
    clReleaseProgram(program);
    clReleaseCommandQueue(cmd_queue);
    clReleaseMemObject(buffer_A);
    clReleaseMemObject(buffer_B);
    clReleaseMemObject(buffer_C);
    clReleaseContext(context);

    free(A);
    free(B);
    free(C);
    free(platforms);
    free(devices);
}

cmake_minimum_required(VERSION 3.8)
project(KIV_ZOS)

set(CMAKE_C_STANDARD 99)

# sources
set(SOURCE_FILES src/main/c/main.c src/main/c/vfs.c src/main/c/vfs.h src/main/c/io.c src/main/c/io.h src/main/c/repl.c src/main/c/repl.h src/main/c/utils.c src/main/c/utils.h src/main/c/vfs-mft.c src/main/c/vfs-mft.h src/main/c/vfs-boot-record.c src/main/c/vfs-boot-record.h src/main/c/vfs-bitmap.c src/main/c/vfs-bitmap.h src/main/c/commands.c src/main/c/commands.h src/main/c/vfs-module-find.c src/main/c/vfs-module-find.h src/main/c/vfs-module-validation.c src/main/c/vfs-module-validation.h src/main/c/vfs-module-allocation.c src/main/c/vfs-module-allocation.h src/main/c/vfs-module-defragmentation.c src/main/c/vfs-module-defragmentation.h src/main/c/vfs-module-utils.c src/main/c/vfs-module-utils.h src/main/c/vfs-module-consistency.c src/main/c/vfs-module-consistency.h)
add_executable(ntfs-dist ${SOURCE_FILES})

# allow pthreads
set(THREADS_PREFER_PTHREAD_FLAG ON)
find_package(Threads REQUIRED)
target_link_libraries(ntfs-dist Threads::Threads)

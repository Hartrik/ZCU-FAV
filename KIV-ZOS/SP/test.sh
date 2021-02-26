#!/bin/bash

./build.sh

PARAMETERS=""   # -r
COMMAND="valgrind --leak-check=full --quiet ./ntfs-dist -t"

cd src/test/bash

./test-flat-no-fragments.sh "${COMMAND}" ${PARAMETERS}
./test-flat-fragments.sh "${COMMAND}" ${PARAMETERS}
./test-flat-more-mft-items.sh "${COMMAND}" ${PARAMETERS}
./test-flat-dirs.sh "${COMMAND}" ${PARAMETERS}
./test-flat-empty-file.sh "${COMMAND}" ${PARAMETERS}
./test-flat-random-data.sh "${COMMAND}" ${PARAMETERS}

./test-dir-tree.sh "${COMMAND}" ${PARAMETERS}
./test-paths-in-commands.sh "${COMMAND}" ${PARAMETERS}

./test-load.sh "${COMMAND}" ${PARAMETERS}
./test-cp.sh "${COMMAND}" ${PARAMETERS}
./test-reallocate.sh "${COMMAND}" ${PARAMETERS}
./test-defragment-1.sh "${COMMAND}" ${PARAMETERS}
./test-defragment-2.sh "${COMMAND}" ${PARAMETERS}
./test-defragment-3.sh "${COMMAND}" ${PARAMETERS}

#./large-test.sh "${COMMAND}" ${PARAMETERS}

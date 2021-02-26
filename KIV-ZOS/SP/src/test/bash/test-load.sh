#!/bin/bash

# Test příkazu load.

TEST_NAME=$(basename "$0" ".sh")
COMMAND="$1"

test $# -eq 2 -a "$2" == "-r"
GENERATE=$?

LOG_FILE=""
if test ${GENERATE} -eq 0
then
    LOG_FILE=${TEST_NAME}.log
else
    LOG_FILE=${TEST_NAME}.tmp
fi


({
cd ../../../build/

echo "AAAAAAA" > in-file-1.tmp
echo "BBBBBBBBBBBB" > in-file-2.tmp

SCRIPT="script.tmp"
echo "init 5120" > ${SCRIPT}
echo "" >> ${SCRIPT}
echo "show parameters" >> ${SCRIPT}
echo "incp in-file-1.tmp f1" >> ${SCRIPT}
echo "incp in-file-2.tmp f2" >> ${SCRIPT}

${COMMAND} "${TEST_NAME}.vfs" <<EOF

load ${SCRIPT}
cat f1
cat f2

check consistency

EOF

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

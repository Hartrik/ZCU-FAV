#!/bin/bash

# Test příkazu reallocate.

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

${COMMAND} "${TEST_NAME}.vfs" <<EOF

init 50 8 25
disable first-fit
show parameters

show mft
show bitmap

incp in-file-1.tmp f1
incp in-file-1.tmp f2
incp in-file-1.tmp f3
incp in-file-1.tmp f4
incp in-file-1.tmp f5
incp in-file-1.tmp f6
incp in-file-1.tmp f7
incp in-file-1.tmp f8
incp in-file-1.tmp f9
incp in-file-1.tmp f10

ls

show mft
show bitmap

reallocate /

show mft
show bitmap

ls

check consistency

EOF

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

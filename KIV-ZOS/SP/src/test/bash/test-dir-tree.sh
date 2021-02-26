#!/bin/bash

# Test jednoduché adresářové strukutry.

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
echo "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC" > in-file-3.tmp

${COMMAND} "${TEST_NAME}.vfs" <<EOF

init 5120
show parameters

pwd

mkdir 1
mkdir 2
mkdir 3

cd 2
pwd
mkdir 2.1
mkdir 2.2
mkdir 2.3

cd 2.2
pwd
mkdir b
ls

cd /
pwd
ls

cd 2/2.2
pwd
ls

cd /2/2.2
pwd
ls

cd /2/2.2/
pwd
ls

cd xxx
cd /2/xxx

check consistency

EOF

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

#!/bin/bash

# Test s náhodnými binárními daty.

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

head -c 99 /dev/urandom > in-file-1.tmp
head -c 999 /dev/urandom > in-file-2.tmp
head -c 9999 /dev/urandom > in-file-3.tmp

${COMMAND} "${TEST_NAME}.vfs" <<EOF

init 25 512 5
show parameters

incp in-file-1.tmp f1
incp in-file-2.tmp f2
incp in-file-3.tmp f3

show mft
df

outcp f1 out-file-1.tmp
outcp f2 out-file-2.tmp
outcp f3 out-file-3.tmp

check consistency

EOF

diff in-file-1.tmp out-file-1.tmp
diff in-file-2.tmp out-file-2.tmp
diff in-file-3.tmp out-file-3.tmp

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

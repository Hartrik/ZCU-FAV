#!/bin/bash

# Test s velkými soubory.

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

head -c 50M /dev/urandom > in-file-1.tmp

${COMMAND} "${TEST_NAME}.vfs" <<EOF

init 21000 5120 10
show parameters

mkdir dir
incp in-file-1.tmp /dir/f1
mv /dir/f1 /dir/f2
cp /dir/f2 /dir/f1

show mft
df

outcp /dir/f1 out-file-1.tmp
outcp /dir/f2 out-file-2.tmp

check consistency

EOF

diff in-file-1.tmp out-file-1.tmp
diff in-file-1.tmp out-file-2.tmp

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

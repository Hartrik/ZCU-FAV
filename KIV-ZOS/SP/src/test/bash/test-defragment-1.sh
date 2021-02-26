#!/bin/bash

# Test defragmentace.

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
echo "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC" > in-file-2.tmp

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
incp in-file-1.tmp f11
incp in-file-1.tmp f12
incp in-file-1.tmp f13
incp in-file-1.tmp f14
incp in-file-1.tmp f15
incp in-file-1.tmp f16
incp in-file-1.tmp f17
incp in-file-1.tmp f18

show mft
show bitmap

rm f1
rm f3
rm f5
rm f7
rm f9
rm f11
rm f13
rm f15
rm f17

incp in-file-2.tmp f50

show mft
show bitmap

defragment

show mft
show bitmap

cat f2
cat f50

check consistency

EOF

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

#!/bin/bash

# Testuje příkaz cp.

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

echo "AAAAAAAAAAAA" > in-file-1.tmp
echo "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC" > in-file-2.tmp
echo "DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD" > in-file-3.tmp

${COMMAND} "${TEST_NAME}.vfs" <<EOF

init 50 8 15
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

show mft
show bitmap

rm f1
rm f3
rm f5
rm f7
rm f9

show mft
show bitmap

incp in-file-2.tmp f50
cp f50 f51

show mft
show bitmap

info f50
info f51

cat f50
cat f51

outcp f50 out-file-50.tmp

rm f50
incp in-file-3.tmp f52

outcp f51 out-file-51.tmp
outcp f52 out-file-52.tmp

show mft
show bitmap

check consistency

EOF

diff in-file-2.tmp out-file-50.tmp
diff in-file-2.tmp out-file-51.tmp
diff in-file-3.tmp out-file-52.tmp

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

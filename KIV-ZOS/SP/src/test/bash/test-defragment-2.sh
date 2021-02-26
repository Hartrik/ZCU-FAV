#!/bin/bash

# Test defragmentace. Více fragmentů, akorát volného místa.

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

echo "123456" > in-file-1.tmp
echo "abcdefghijklmnopqrst" > in-file-2.tmp

${COMMAND} "${TEST_NAME}.vfs" <<EOF

init 26 8 15
disable first-fit
show parameters

show mft
show bitmap

mkdir t1
incp in-file-1.tmp /t1/f1.1
incp in-file-1.tmp /t1/f1.2
incp in-file-1.tmp /t1/f1.3

mkdir t2
incp in-file-1.tmp /t2/f2.1
incp in-file-1.tmp /t2/f2.2
incp in-file-1.tmp /t2/f2.3

mkdir t3
incp in-file-1.tmp /t3/f3.1
incp in-file-1.tmp /t3/f3.2
incp in-file-1.tmp /t3/f3.3

show mft
show bitmap

rm /t1/f1.2
incp in-file-2.tmp /t1/f1.2

rm /t2/f2.2
incp in-file-2.tmp /t2/f2.2

rm /t3/f3.2
incp in-file-2.tmp /t3/f3.2

show mft
show bitmap

cat /t1/f1.1
cat /t1/f1.3
cat /t2/f2.1
cat /t2/f2.3
cat /t3/f3.1
cat /t3/f3.3

cat /t1/f1.2
cat /t2/f2.2
cat /t3/f3.2

defragment

show mft
show bitmap

cat /t1/f1.1
cat /t1/f1.3
cat /t2/f2.1
cat /t2/f2.3
cat /t3/f3.1
cat /t3/f3.3

cat /t1/f1.2
cat /t2/f2.2
cat /t3/f3.2

check consistency

EOF

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

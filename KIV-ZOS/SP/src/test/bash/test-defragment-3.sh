#!/bin/bash

# Test defragmentace. Více fragmentů, akorát volného místa, s dírou uprostřed.
# + opětovné načtení vfs

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

echo "-0001-" > in-file-1.tmp
echo "-0002-" > in-file-2.tmp
echo "-0003-" > in-file-3.tmp
echo "-0004-" > in-file-4.tmp
echo "-0005-" > in-file-5.tmp
echo "-0006-" > in-file-6.tmp
echo "-0007-" > in-file-7.tmp
echo "-0008-" > in-file-8.tmp
echo "-0009-" > in-file-9.tmp
echo "-10-abcdefghijklmnop" > in-file-10.tmp
echo "-11-abcdefghijklmnop" > in-file-11.tmp
echo "-12-abcdefghijklmnop" > in-file-12.tmp

${COMMAND} "${TEST_NAME}.vfs" <<EOF

init 23 8 15
disable first-fit
show parameters

show mft
show bitmap

mkdir t1
incp in-file-1.tmp /t1/f1.1
incp in-file-2.tmp /t1/f1.2
incp in-file-3.tmp /t1/f1.3

mkdir t2
incp in-file-4.tmp /t2/f2.1
incp in-file-5.tmp /t2/f2.2
incp in-file-6.tmp /t2/f2.3

mkdir t3
incp in-file-7.tmp /t3/f3.1
incp in-file-8.tmp /t3/f3.2
incp in-file-9.tmp /t3/f3.3

show mft
show bitmap

rm /t1/f1.2
incp in-file-10.tmp /t1/f1.2

rm /t2/f2.2
incp in-file-11.tmp /t2/f2.2

rm /t3/f3.2
incp in-file-12.tmp /t3/f3.2

rm /t2/f2.2

show mft
show bitmap

cat /t1/f1.1
cat /t1/f1.3
cat /t2/f2.1
cat /t2/f2.3
cat /t3/f3.1
cat /t3/f3.3

cat /t1/f1.2
cat /t3/f3.2

ls
ls t1
ls t2
ls t3

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
cat /t3/f3.2

ls
ls t1
ls t2
ls t3

EOF

echo "--- REOPEN ---"

${COMMAND} "${TEST_NAME}.vfs" <<EOF

show mft
show bitmap

cat /t1/f1.1
cat /t1/f1.3
cat /t2/f2.1
cat /t2/f2.3
cat /t3/f3.1
cat /t3/f3.3

cat /t1/f1.2
cat /t3/f3.2

ls
ls t1
ls t2
ls t3

EOF

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

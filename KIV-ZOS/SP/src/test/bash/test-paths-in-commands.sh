#!/bin/bash

# Test podpory celých cest v příkazech.

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

${COMMAND} "${TEST_NAME}.vfs" <<EOF

init 5120
show parameters

mkdir aaa
mkdir aaa/bbb
mkdir aaa/bbb/111
mkdir aaa/bbb/222

cd aaa
pwd

incp in-file-1.tmp bbb/111/f1
incp in-file-2.tmp /aaa/bbb/111/f2

ls bbb/111

mv bbb/111/f2 bbb/222/f2

ls /aaa/bbb/111
ls /aaa/bbb/222

info bbb/111/f1
info /aaa/bbb/222/f2

cp /aaa/bbb/222/f2 /f3

cat bbb/111/f1
cat /aaa/bbb/222/f2
cat /f3

outcp bbb/111/f1 out-file-1.tmp
outcp /aaa/bbb/222/f2 out-file-2.tmp

rm bbb/111/f1
rm /aaa/bbb/222/f2
rm /f3

ls /aaa/bbb/111

rmdir bbb/111
rmdir /aaa/bbb/222
rmdir /aaa/bbb/
rmdir /aaa
cd /
rmdir /aaa

show mft

check consistency

EOF

diff in-file-1.tmp out-file-1.tmp
diff in-file-2.tmp out-file-2.tmp

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

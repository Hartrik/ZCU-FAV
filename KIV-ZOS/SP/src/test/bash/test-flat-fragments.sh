#!/bin/bash

# Jednoduchá práce se soubory.
# Více fragmentů. Bez použití adresářů.

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
echo "BBBBBBBBBBBBBBB" > in-file-2.tmp
echo "CCCCCCCCCCCCCCCCCCCCCCC" > in-file-3.tmp

${COMMAND} "${TEST_NAME}.vfs" <<EOF

init 15 8 9
disable first-fit
show parameters

df
show mft
show bitmap

incp in-file-1.tmp f1
incp in-file-1.tmp f2
incp in-file-2.tmp f3
incp in-file-2.tmp f4
incp in-file-2.tmp f5
incp in-file-1.tmp f6
incp in-file-1.tmp f7

df
show mft
show bitmap

rm f2
rm f4
rm f6

df
show mft
show bitmap

incp in-file-3.tmp f11
incp in-file-3.tmp f12

cat f11
cat f12
outcp f11 out-file-11.tmp
outcp f12 out-file-12.tmp

df
show mft
show bitmap

info f11
info f12

check consistency

EOF

diff in-file-3.tmp out-file-11.tmp
diff in-file-3.tmp out-file-12.tmp

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

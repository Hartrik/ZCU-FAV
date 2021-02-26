#!/bin/bash

# Jednoduchá práce se soubory.
# Bez tvorby fragmentů. Bez použití adresářů.

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

init 16 8 5
show parameters

df
show mft
show bitmap

incp in-file-1.tmp f1
incp in-file-2.tmp f2

cat f1
cat f2

df
show mft
show bitmap

rm f2
cat f2

df
show mft
show bitmap

incp in-file-3.tmp f3

cat f1
cat f3
outcp f1 out-file-1.tmp
outcp f3 out-file-3.tmp

df
show mft
show bitmap

check consistency

EOF

diff in-file-1.tmp out-file-1.tmp
diff in-file-3.tmp out-file-3.tmp

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

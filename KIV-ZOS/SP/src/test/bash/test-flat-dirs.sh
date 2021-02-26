#!/bin/bash

# Jednoduchá práce se soubory a adresáři.

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

init 15 8 9
show parameters

incp in-file-1.tmp f1
incp in-file-2.tmp f2

mkdir d1
mkdir d1
mkdir d2

show mft

ls

rm f1
rm XXX
rm d1

rmdir d1
rmdir XXX
rmdir f2

show mft

ls

check consistency

EOF

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

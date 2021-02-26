#!/bin/bash

# Testuje práci s prázdnými soubory.

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
touch in-file-empty.tmp

${COMMAND} "${TEST_NAME}.vfs" <<EOF

init 10 256 5
show parameters

incp in-file-empty.tmp empty-1

ls
info empty-1

cp empty-1 empty-2
cp empty-1 empty-4
mv empty-4 empty-3

ls
show mft

cat empty-1
cat empty-2
cat empty-3

outcp empty-1 out-file-1.tmp
outcp empty-2 out-file-2.tmp
outcp empty-3 out-file-3.tmp

check consistency

EOF

diff in-file-empty.tmp out-file-1.tmp
diff in-file-empty.tmp out-file-2.tmp
diff in-file-empty.tmp out-file-3.tmp

} > ${LOG_FILE})

if test ${GENERATE} -eq 1
then
    icdiff --highlight --line-numbers ${TEST_NAME}.log ${TEST_NAME}.tmp

    diff ${TEST_NAME}.log ${TEST_NAME}.tmp > /dev/null
    exit $?
fi

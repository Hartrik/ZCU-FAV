#!/bin/bash

cd build/

if test $# -gt 0
then
    if test $1 = "clean"
    then
        rm -f games.pzl
    else
        echo "Unknown parameter: '$1'"
    fi
fi

./server-dist

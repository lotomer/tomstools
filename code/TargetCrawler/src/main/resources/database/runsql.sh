#!/bin/sh
USER=root
PASSWORD=root123
HOST=127.0.0.1
DATABASE=bigcloud
CHARSET=utf8
CMD=mysql
DIR=$1
if [ $# != 1 ]; then
    echo invalid arguments!
    echo Usage:
    echo "    $0 directory"
    exit 1
fi
for sqlFileName in `find $DIR -name "*.sql"`
do
    echo will execute: $CMD -h$HOST -u$USER -p$PASSWORD --default-character-set=$CHARSET -D$DATABASE \< "$sqlFileName"
    $CMD -h$HOST -u$USER -p$PASSWORD --default-character-set=$CHARSET -D$DATABASE < "$sqlFileName"
done
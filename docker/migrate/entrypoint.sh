#!/bin/sh

command=$1
option=$2
migrate -path /migrations/ -database "${DATABASE_URL}" ${command:-up} ${option}

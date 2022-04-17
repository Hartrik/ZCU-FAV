#!/bin/sh

printf "Simple example with only two nodes.\n"
printf "Starting...\n"
nohup java -jar target/server-1.0.0-SNAPSHOT.jar 8081 *:8091 localhost:8092 1>log-server-1.txt 2>&1 &
nohup java -jar target/server-1.0.0-SNAPSHOT.jar 8082 *:8092 localhost:8091 1>log-server-2.txt 2>&1 &
sleep 5s

printf "\nCheck status...\n"

printf "1: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8081/status
printf "\n"

printf "2: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8082/status
printf "\n"

sleep 1s

printf "\nCreate snapshot...\n"

curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8081/snapshot/42
printf "\n"

printf "\nWaiting for snapshot to complete...\n"
sleep 2s

printf "\nPrint snapshot...\n"
printf "1: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8081/snapshot/42/status
printf "\n"
printf "2: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8082/snapshot/42/status
printf "\n"

sleep 1s

printf "\nShutting down nodes...\n"
printf "1: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8081/shutdown
printf "\n"
printf "2: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8082/shutdown
printf "\n"

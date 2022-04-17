#!/bin/sh

printf "Simple example with four nodes:\n"
printf "1---------2\n"
printf "|     __/ |\n"
printf "|  __/    |\n"
printf "| /       |\n"
printf "4---------3\n"
printf "\n"

printf "Starting...\n"
nohup java -jar target/server-1.0.0-SNAPSHOT.jar 8081 *:8091,*:8092 localhost:8093,localhost:8098 1>log-server-1.txt 2>&1 &
nohup java -jar target/server-1.0.0-SNAPSHOT.jar 8082 *:8093,*:8094,*:8100 localhost:8091,localhost:8095,localhost:8099 1>log-server-2.txt 2>&1 &
nohup java -jar target/server-1.0.0-SNAPSHOT.jar 8083 *:8095,*:8096 localhost:8094,localhost:8097 1>log-server-3.txt 2>&1 &
nohup java -jar target/server-1.0.0-SNAPSHOT.jar 8084 *:8097,*:8098,*:8099 localhost:8096,localhost:8092,localhost:8100 1>log-server-4.txt 2>&1 &
sleep 5s

printf "\nCheck status...\n"

printf "1: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8081/status
printf "\n"
printf "2: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8082/status
printf "\n"
printf "3: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8083/status
printf "\n"
printf "4: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8084/status
printf "\n"

sleep 1s

printf "\nCreate snapshot...\n"

curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8081/snapshot/42
printf "\n"

printf "\nWaiting for snapshot to complete...\n"
sleep 7s

printf "\nPrint snapshot...\n"
printf "1: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8081/snapshot/42/status
printf "\n"
printf "2: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8082/snapshot/42/status
printf "\n"
printf "3: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8083/snapshot/42/status
printf "\n"
printf "4: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8084/snapshot/42/status
printf "\n"

sleep 1s

printf "\nShutting down nodes...\n"
printf "1: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8081/shutdown
printf "\n"
printf "2: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8082/shutdown
printf "\n"
printf "3: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8083/shutdown
printf "\n"
printf "4: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8084/shutdown
printf "\n"

#!/bin/bash

snapshot_id=$((RANDOM))
printf "\nCreate snapshot with id=$snapshot_id...\n"

curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET "http://10.0.3.61:8081/snapshot/$snapshot_id"
printf "\n"

printf "\nWaiting for snapshot to complete...\n"
sleep 10s

printf "\nPrint snapshot...\n"
printf "1: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET "http://10.0.3.61:8081/snapshot/$snapshot_id/status"
printf "\n"
printf "2: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET "http://10.0.3.62:8082/snapshot/$snapshot_id/status"
printf "\n"
printf "3: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET "http://10.0.3.63:8083/snapshot/$snapshot_id/status"
printf "\n"
printf "4: "
curl -H "Accept: application/json" -H "Content-Type: application/json" -X GET "http://10.0.3.64:8084/snapshot/$snapshot_id/status"
printf "\n"

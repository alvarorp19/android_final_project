#!/bin/bash

while true; do
    docker exec -it mosquitto_broker mosquitto_pub -h localhost -p 1883 -t "android/topic" -m "hello from the broker"
    echo "Message sent at $(date)"
    sleep 5
done

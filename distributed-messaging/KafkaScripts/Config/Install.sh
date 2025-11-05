#!/bin/bash

cd /opt
wget https://dlcdn.apache.org/kafka/4.1.0/kafka_2.13-4.1.0.tgz
tar -xvf kafka_2.13-4.1.0.tgz
mv kafka_2.13-4.1.0 kafka
rm kafka_2.13-4.1.0.tgz

rm -rf /tmp/kraft-combined-logs
mkdir -p /tmp/kraft-combined-logs

UUID=$(/opt/kafka/bin/kafka-storage.sh random-uuid)
echo "Nouvel UUID: $UUID"

/opt/kafka/bin/kafka-storage.sh format -t $UUID -c /opt/kafka/config/server.properties --standalone
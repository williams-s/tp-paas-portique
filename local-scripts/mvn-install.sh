#!/bin/bash

cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd
cd ..
CUR_DIR=$(pwd)

echo "Repertoire du projet :" ${CUR_DIR}

echo -e '\033[107m\033[1;94m'Installation des (6) modules...'\033[0m'

echo -e '\033[107m\033[1;94m'1/6 Installation de badge-sensor-mock...'\033[0m'
cd badge-sensor-mock
mvn clean install -DskipTests

echo -e '\033[107m\033[1;94m'2/6 Installation de core-operational-backend...'\033[0m'
cd ${CUR_DIR}
cd core-and-redis/core-operational-backend
mvn clean install -DskipTests

echo -e '\033[107m\033[1;94m'3/6 Installation de cache-loading-backend...'\033[0m'
cd ${CUR_DIR}
cd database-service/cache-loading-backend
mvn clean install -DskipTests

echo -e '\033[107m\033[1;94m'4/6 Installation de entrance-door-lock...'\033[0m'
cd ${CUR_DIR}
cd entrance-door-lock
mvn clean install -DskipTests

echo -e '\033[107m\033[1;94m'5/6 Installation de telemetry-to-messaging-backend...'\033[0m'
cd ${CUR_DIR}
cd telemetry-and-mqtt/core-operational-backend
mvn clean install -DskipTests




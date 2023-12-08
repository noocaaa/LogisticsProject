#!/bin/bash

set -a 
source ./src/main/resources/.env
set +a

mvn clean install

cd target
java -jar logistics-0.0.1-SNAPSHOT.jar


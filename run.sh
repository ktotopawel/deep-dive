#!/bin/bash

# 1. Load the .env file
if [ -f .env ]; then
  set -a
  source .env
  set +a
else
  echo "Warning: .env file not found."
fi

export SPRING_PROFILES_ACTIVE=classifier

./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="./src/main/resources/dataset/dataset.csv"

#!/bin/sh

./gradlew test jacocoTestReport
./gradlew sonarqube \
  -Dsonar.projectKey=myddd-vertx \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=7731f2c2a08483888c254595c15ea5ff99ba704a

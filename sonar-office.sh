#!/bin/sh

./gradlew test jacocoTestReport
./gradlew sonarqube \
  -Dsonar.projectKey=myddd-vertx \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=3591ce64e0bb1ebe59dbf751fc9492c771782918

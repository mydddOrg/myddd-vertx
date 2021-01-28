#!/bin/sh


./gradlew test jacocoTestReport
./gradlew sonarqube \
  -Dsonar.projectKey=myddd-vertx \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=a9fa4fe2b9949a040708b4e84355ccded127848e

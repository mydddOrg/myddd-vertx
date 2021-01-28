#!/bin/sh


./gradlew test jacocoTestReport
./gradlew sonarqube \
  -Dsonar.projectKey=myddd-vertx \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=df65c488aaad40af24cf64b10d6558ab7d035c42

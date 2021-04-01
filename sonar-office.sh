#!/bin/sh

./gradlew test jacocoTestReport
./gradlew sonarqube \
  -Dsonar.projectKey=myddd-vertx \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=88b16c604824d25ed052b067a84bd2835aa67f54

#!/bin/sh


./gradlew test jacocoTestReport
./gradlew sonarqube \
  -Dsonar.projectKey=myddd-vertx \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=f0740ee9bd78a74d3e3e8530e1fbe3609a653524
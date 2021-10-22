#!/bin/sh

./gradlew test jacocoTestReport && echo "测试成功" || exit 1
./gradlew sonarqube \
  -Dsonar.projectKey=myddd-vertx \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=f9c74d911fcd72c9a2994094feaf551792fbc627

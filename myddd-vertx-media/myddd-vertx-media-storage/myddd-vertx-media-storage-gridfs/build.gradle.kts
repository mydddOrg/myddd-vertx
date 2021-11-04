plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":myddd-vertx-media:myddd-vertx-media-domain"))
    implementation(project(":myddd-vertx-base:myddd-vertx-base-config"))


    implementation("io.vertx:vertx-mongo-client:${rootProject.extra["vertx_version"]}")

    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    api(project(":myddd-vertx-base:myddd-vertx-base-api"))

    testImplementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))
}

sonarqube {
    isSkipProject = true
}
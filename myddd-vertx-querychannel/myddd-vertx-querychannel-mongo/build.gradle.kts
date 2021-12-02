plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":myddd-vertx-domain"))
    implementation(project(":myddd-vertx-querychannel:myddd-vertx-querychannel-api"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    implementation(project(":myddd-vertx-base:myddd-vertx-base-api"))
    api("io.vertx:vertx-mongo-client:${rootProject.extra["vertx_version"]}")


    testImplementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))
    testImplementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))
    testImplementation(project(":myddd-vertx-repository:myddd-vertx-repository-mongo"))
}

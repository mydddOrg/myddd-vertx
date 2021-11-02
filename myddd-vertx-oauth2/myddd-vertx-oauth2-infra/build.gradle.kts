plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-domain"))


    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))
    api(project(":myddd-vertx-repository:myddd-vertx-repository-hibernate"))

    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    testImplementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))

    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    api(project(":myddd-vertx-base:myddd-vertx-base-provider"))

    testImplementation("io.vertx:vertx-pg-client:${rootProject.extra["vertx_version"]}")
    testImplementation("io.vertx:vertx-mysql-client:${rootProject.extra["vertx_version"]}")
}

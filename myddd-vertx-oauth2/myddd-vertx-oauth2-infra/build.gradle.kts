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

    api(project(":myddd-vertx-base:myddd-vertx-base-provider"))
    testImplementation("io.vertx:vertx-mysql-client:${rootProject.extra["vertx_version"]}")
}

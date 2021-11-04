plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

extra["publishJar"] = true


dependencies {
    implementation(kotlin("stdlib"))

    api("io.vertx:vertx-config:${rootProject.extra["vertx_version"]}")
    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))

    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
}

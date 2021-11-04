plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
extra["publishJar"] = true


dependencies {
    //base
    implementation(kotlin("stdlib"))

    //api
    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
    implementation(project(":myddd-vertx-cache:myddd-vertx-cache-api"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
}

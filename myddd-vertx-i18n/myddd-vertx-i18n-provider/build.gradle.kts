plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))
    api("javax.inject:javax.inject:1")

    implementation("io.vertx:vertx-config:${rootProject.extra["vertx_version"]}")
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))

    implementation(project(":myddd-vertx-i18n:myddd-vertx-i18n-api"))
    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")

}

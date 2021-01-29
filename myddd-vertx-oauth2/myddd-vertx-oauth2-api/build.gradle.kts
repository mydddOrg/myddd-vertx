plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")

    api("io.vertx:vertx-auth-common:${rootProject.extra["vertx_version"]}")
}
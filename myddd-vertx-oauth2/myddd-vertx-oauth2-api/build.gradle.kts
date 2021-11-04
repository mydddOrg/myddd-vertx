plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))

    api("io.vertx:vertx-auth-common:${rootProject.extra["vertx_version"]}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${rootProject.extra["jackson_version"]}")
}

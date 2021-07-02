plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")

    api("io.vertx:vertx-auth-common:${rootProject.extra["vertx_version"]}")

    implementation("com.fasterxml.jackson.core:jackson-annotations:${rootProject.extra["jackson_version"]}")

}

plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

dependencies {
    implementation(kotlin("stdlib"))

    api("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")

    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))

    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    api("org.eclipse.persistence:javax.persistence:${rootProject.extra["javax_persistence_version"]}")
}

plugins {
    java
    kotlin("jvm")
}

group = "cc.lingenliu"
version = rootProject.extra["version"]!!

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")

    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))

    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    implementation("org.eclipse.persistence:javax.persistence:2.2.1")
}

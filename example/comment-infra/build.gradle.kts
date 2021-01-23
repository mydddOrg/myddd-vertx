plugins {
    java
    kotlin("jvm")
}

group = "cc.lingenliu"
version = rootProject.extra["version"]!!

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":example:comment-domain"))

    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))
    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-hibernate"))

    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    testImplementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))

    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
}

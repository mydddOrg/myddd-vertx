plugins {
    java
    kotlin("jvm")
}

group = "cc.lingenliu"
version = rootProject.extra["version"]!!

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")

    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
}

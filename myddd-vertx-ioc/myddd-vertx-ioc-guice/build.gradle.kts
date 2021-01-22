plugins {
    java
    kotlin("jvm")
}

group = "cc.lingenliu"
version = rootProject.extra["version"]!!

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    api("com.google.inject:guice:5.0.0-BETA-1")
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
}

plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":myddd-vertx-media:myddd-vertx-media-domain"))
    implementation("com.fasterxml.jackson.core:jackson-annotations:${rootProject.extra["jackson_version"]}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${rootProject.extra["jackson_version"]}")


    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    api(project(":myddd-vertx-base:myddd-vertx-base-api"))

    testImplementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))

}

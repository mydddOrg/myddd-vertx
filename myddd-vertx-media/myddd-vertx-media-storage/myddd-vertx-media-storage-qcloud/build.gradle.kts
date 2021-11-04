plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
extra["publishJar"] = true

extra["cos_api_version"] = "5.6.38"

dependencies {
    implementation(kotlin("stdlib"))

    implementation("com.qcloud:cos_api:${project.extra["cos_api_version"]}")

    implementation(project(":myddd-vertx-media:myddd-vertx-media-domain"))
    implementation("com.fasterxml.jackson.core:jackson-annotations:${rootProject.extra["jackson_version"]}")

    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    api(project(":myddd-vertx-base:myddd-vertx-base-api"))

    testImplementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))

}

sonarqube {
    isSkipProject = true
}

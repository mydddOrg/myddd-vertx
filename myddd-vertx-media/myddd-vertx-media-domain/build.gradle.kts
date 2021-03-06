plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))

    api(project(":myddd-vertx-domain"))
    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    api(project(":myddd-vertx-base:myddd-vertx-base-api"))
    api(project(":myddd-vertx-repository:myddd-vertx-repository-api"))


    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    api("org.eclipse.persistence:javax.persistence:${rootProject.extra["javax_persistence_version"]}")

    testImplementation(project(":myddd-vertx-media:myddd-vertx-media-infra"))
    testImplementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))
    testImplementation(project(":myddd-vertx-media:myddd-vertx-media-storage:myddd-vertx-media-storage-local"))
    testImplementation(project(":myddd-vertx-repository:myddd-vertx-repository-hibernate"))

    testImplementation("io.vertx:vertx-mysql-client:${rootProject.extra["vertx_version"]}")

}
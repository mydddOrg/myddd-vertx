plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

extra["publishJar"] = true


dependencies {
    implementation(kotlin("stdlib"))

    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    api(project(":myddd-vertx-base:myddd-vertx-base-api"))
    api("org.eclipse.persistence:javax.persistence:${rootProject.extra["javax_persistence_version"]}")


    testImplementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))
}

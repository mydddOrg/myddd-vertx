plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":myddd-vertx-domain"))
    api(project(":myddd-vertx-repository:myddd-vertx-repository-api"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    implementation(project(":myddd-vertx-base:myddd-vertx-base-api"))

    api("org.hibernate.reactive:hibernate-reactive-core:${rootProject.extra["hibernate_reactive_version"]}")

    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")

    testImplementation("javax.inject:javax.inject:1")
    testImplementation("org.eclipse.persistence:javax.persistence:${rootProject.extra["javax_persistence_version"]}")
    testImplementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))
    testImplementation("io.vertx:vertx-mysql-client:${rootProject.extra["vertx_version"]}")

}
plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":myddd-vertx-querychannel:myddd-vertx-querychannel-api"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))


    implementation(project(":myddd-vertx-base:myddd-vertx-base-api"))

    implementation("org.hibernate.reactive:hibernate-reactive-core:${rootProject.extra["hibernate_reactive_version"]}")

    testImplementation("javax.inject:javax.inject:1")
    testImplementation("org.eclipse.persistence:javax.persistence:${rootProject.extra["javax_persistence_version"]}")

    implementation("io.smallrye.reactive:mutiny:0.14.0")
    implementation("com.ibm.async:asyncutil:0.1.0")

    testImplementation(project(":myddd-vertx-domain"))
    testImplementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))
    testImplementation(project(":myddd-vertx-repository:myddd-vertx-repository-hibernate"))
    testImplementation("io.vertx:vertx-mysql-client:${rootProject.extra["vertx_version"]}")
    testImplementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))
}

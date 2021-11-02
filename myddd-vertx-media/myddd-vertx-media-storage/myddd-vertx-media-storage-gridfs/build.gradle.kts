plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")
    implementation(project(":myddd-vertx-media:myddd-vertx-media-domain"))
    implementation(project(":myddd-vertx-base:myddd-vertx-base-config"))


    implementation("io.vertx:vertx-mongo-client:${rootProject.extra["vertx_version"]}")

    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    api(project(":myddd-vertx-base:myddd-vertx-base-api"))

    testImplementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))
    testImplementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))
    testImplementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")


}

sonarqube {
    isSkipProject = true
}
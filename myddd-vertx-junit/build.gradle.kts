plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

extra["publishJar"] = true

dependencies {
    //base
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
    implementation(project(":myddd-vertx-domain"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))

    //api
    api("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    api("org.junit.jupiter:junit-jupiter:${rootProject.extra["junit5_version"]}")
    api("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    api("org.assertj:assertj-core:${rootProject.extra["assertj-core-version"]}")
    api("org.mockito:mockito-core:${rootProject.extra["mockito_version"]}")

    testImplementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))
}

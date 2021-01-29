plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    api("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-auth-oauth2:${rootProject.extra["vertx_version"]}")

    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")

    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-domain"))
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-api"))

    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))

    testImplementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))

    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))


    testImplementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-infra"))
    testImplementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-application"))

}
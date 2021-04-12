plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx.example"
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
    api("io.vertx:vertx-lang-kotlin:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")

    api(project(":myddd-vertx-domain"))
    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))

    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))
    api(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-domain"))
    api(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-infra"))

    testImplementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-domain"))
    testImplementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))
    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation(project(":example:document-infra"))

    testImplementation("io.vertx:vertx-pg-client:${rootProject.extra["vertx_version"]}")


}

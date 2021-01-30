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

    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")

    implementation("io.vertx:vertx-web:${rootProject.extra["vertx_version"]}")

    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))

    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))
    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-hibernate"))

    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-domain"))
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-api"))
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-application"))
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-infra"))

    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")

    implementation("javax.inject:javax.inject:1")
    implementation("org.eclipse.persistence:javax.persistence:2.2.1")

    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation("io.vertx:vertx-web-client:${rootProject.extra["vertx_version"]}")


}

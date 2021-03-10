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


    //base
    api("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")

    //framework
    api("io.vertx:vertx-web:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-config:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-config-yaml:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-web-validation:${rootProject.extra["vertx_version"]}")

    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
    implementation("io.vertx:vertx-json-schema:${rootProject.extra["vertx_version"]}")

    //api
    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    api(project(":myddd-vertx-i18n:myddd-vertx-i18n-api"))
    api(project(":myddd-vertx-base:myddd-vertx-base-api"))

    //api implement
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))
    implementation(project(":myddd-vertx-i18n:myddd-vertx-i18n-provider"))
    implementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-api"))


    //test
    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation("io.vertx:vertx-web-client:${rootProject.extra["vertx_version"]}")
    testImplementation("org.mockito:mockito-core:${rootProject.extra["mockito_version"]}")

    testImplementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-domain"))
    testImplementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-application"))
    testImplementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-infra"))


}

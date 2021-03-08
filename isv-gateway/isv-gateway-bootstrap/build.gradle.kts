plugins {
    java
    kotlin("jvm")
}

group = "com.foreverht.isvgateway"
version = rootProject.extra["isv_gateway_version"]!!

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

    implementation(project(":myddd-vertx-web"))
    implementation(project(":isv-gateway:isv-gateway-domain"))
    implementation(project(":isv-gateway:isv-gateway-api"))
    implementation(project(":isv-gateway:isv-gateway-application"))
    implementation(project(":isv-gateway:isv-gateway-infra"))



    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    implementation(project(":myddd-vertx-i18n:myddd-vertx-i18n-api"))
    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-api"))


    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-hibernate"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))
    implementation(project(":myddd-vertx-i18n:myddd-vertx-i18n-provider"))
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-application"))


    implementation("io.vertx:vertx-json-schema:${rootProject.extra["vertx_version"]}")
    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${rootProject.extra["jackson_version"]}")


    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation("io.vertx:vertx-web-client:${rootProject.extra["vertx_version"]}")

}

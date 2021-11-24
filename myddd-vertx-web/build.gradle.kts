plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
extra["publishJar"] = true

dependencies {
    //base
    implementation("io.vertx:vertx-web-client:${rootProject.extra["vertx_version"]}")

    //framework
    api("io.vertx:vertx-web:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-config:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-web-validation:${rootProject.extra["vertx_version"]}")

    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
    implementation("io.vertx:vertx-json-schema:${rootProject.extra["vertx_version"]}")

    //api
    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    api(project(":myddd-vertx-i18n:myddd-vertx-i18n-api"))
    api(project(":myddd-vertx-base:myddd-vertx-base-api"))
    api(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-api"))
    api("org.eclipse.persistence:javax.persistence:${rootProject.extra["javax_persistence_version"]}")

    //api implement
    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))
    implementation(project(":myddd-vertx-i18n:myddd-vertx-i18n-provider"))
    implementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-application"))


    //test
    testImplementation("io.vertx:vertx-web-client:${rootProject.extra["vertx_version"]}")

}

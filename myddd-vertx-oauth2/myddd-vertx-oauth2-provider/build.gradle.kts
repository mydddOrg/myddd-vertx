plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
extra["publishJar"] = true

//tasks.jacocoTestReport {
//    reports {
//        xml.isEnabled = true
//    }
//}

dependencies {
    implementation(kotlin("stdlib"))
    api("io.vertx:vertx-auth-oauth2:${rootProject.extra["vertx_version"]}")

    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-domain"))
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-api"))

    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))


    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))


    testImplementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-infra"))
    testImplementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-application"))
    testImplementation("io.vertx:vertx-mysql-client:${rootProject.extra["vertx_version"]}")
}

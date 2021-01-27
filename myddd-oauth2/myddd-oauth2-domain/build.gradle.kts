plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

dependencies {
    implementation(kotlin("stdlib"))

    //添加领域层的依赖
    api(project(":myddd-vertx-domain"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))

    testImplementation(project(":myddd-oauth2:myddd-oauth2-domain"))
    testImplementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))
    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation(project(":myddd-oauth2:myddd-oauth2-infra"))

}

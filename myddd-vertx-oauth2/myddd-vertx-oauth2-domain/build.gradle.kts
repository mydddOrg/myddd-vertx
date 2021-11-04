plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))

    //添加领域层的依赖
    api(project(":myddd-vertx-domain"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))

    testImplementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-domain"))
    testImplementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-infra"))
    testImplementation("io.vertx:vertx-mysql-client:${rootProject.extra["vertx_version"]}")
}

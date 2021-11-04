plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))

    //OAuth2领域实现
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-domain"))
    //OAuth2 Api
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-api"))
    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))

    //查询通道
    api(project(":myddd-vertx-querychannel:myddd-vertx-querychannel-api"))
    //查询通道实现
    api(project(":myddd-vertx-querychannel:myddd-vertx-querychannel-hibernate"))


    //依赖注入
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    //依赖注入实现（仅测试下）
    testImplementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-infra"))
    testImplementation("io.vertx:vertx-mysql-client:${rootProject.extra["vertx_version"]}")
}

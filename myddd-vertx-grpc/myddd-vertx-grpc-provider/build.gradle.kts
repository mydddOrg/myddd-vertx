plugins {
    java
    kotlin("jvm")
    id("idea")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
val mySkipTests = true
extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))



    implementation(project(":myddd-vertx-grpc:myddd-vertx-grpc-api"))
    implementation(project(":myddd-vertx-grpc:myddd-vertx-grpc-servicetype"))

    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))

    implementation("com.google.protobuf:protobuf-java:${rootProject.extra["protobuf-java"]}")
    implementation("io.vertx:vertx-grpc:${rootProject.extra["vertx_version"]}")
    implementation("javax.annotation:javax.annotation-api:${rootProject.extra["annotation-api"]}")

    implementation("io.vertx:vertx-service-discovery:${rootProject.extra["vertx_version"]}")

    api(project(":myddd-vertx-base:myddd-vertx-base-api"))
    implementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))


    testImplementation("io.vertx:vertx-service-discovery:${rootProject.extra["vertx_version"]}")
}
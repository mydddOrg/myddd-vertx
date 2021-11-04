plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

extra["publishJar"] = true

dependencies {
    //base
    implementation(kotlin("stdlib"))

    implementation(project(":myddd-vertx-media:myddd-vertx-media-domain"))

    //api
    api(project(":myddd-vertx-repository:myddd-vertx-repository-api"))
    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))

    //api implementation
    api(project(":myddd-vertx-repository:myddd-vertx-repository-hibernate"))
    implementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))

    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${rootProject.extra["jackson_version"]}")
    
    //test
    testImplementation("io.vertx:vertx-mysql-client:${rootProject.extra["vertx_version"]}")
}

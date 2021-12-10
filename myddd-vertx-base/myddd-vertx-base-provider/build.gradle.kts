plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    api(project(":myddd-vertx-base:myddd-vertx-base-config"))

    api(project(":myddd-vertx-base:myddd-vertx-base-api"))
    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))

    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
    //the 3rd lib
    implementation("org.apache.commons:commons-lang3:${rootProject.extra["commons_lang3_version"]}")
    implementation("commons-codec:commons-codec:1.15")
    //ulid kotlin库
    implementation("com.github.guepardoapps:kulid:2.0.0.0")

}

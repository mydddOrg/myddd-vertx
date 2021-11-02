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
    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")


    //api
    api("org.apache.logging.log4j:log4j-api:${rootProject.extra["log4j_version"]}")
    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))

    //test
    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation("org.mockito:mockito-core:${rootProject.extra["mockito_version"]}")
}

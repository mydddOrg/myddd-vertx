plugins {
    java
    kotlin("jvm")
}

group = "com.foreverht.isvgateway"
version = rootProject.extra["isv_gateway_version"]!!

dependencies {
    implementation(kotlin("stdlib"))

    api("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")


    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
}

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

    //api
    implementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    implementation("org.junit.jupiter:junit-jupiter:${rootProject.extra["junit5_version"]}")
    implementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    implementation("org.assertj:assertj-core:${rootProject.extra["assertj-core-version"]}")
}

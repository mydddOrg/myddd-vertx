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
    api("org.apache.logging.log4j:log4j-api:${rootProject.extra["log4j_version"]}")
}

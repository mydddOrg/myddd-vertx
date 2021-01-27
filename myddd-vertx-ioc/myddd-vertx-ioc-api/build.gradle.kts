plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!


dependencies {
    implementation(kotlin("stdlib"))
    api("javax.inject:javax.inject:1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
}

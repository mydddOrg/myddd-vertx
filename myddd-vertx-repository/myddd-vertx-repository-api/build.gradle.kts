plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
val mySkipTests = true
extra["publishJar"] = true

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":myddd-vertx-domain"))
}

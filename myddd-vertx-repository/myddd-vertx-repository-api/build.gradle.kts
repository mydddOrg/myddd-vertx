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
    api(project(":myddd-vertx-domain"))
    implementation("io.smallrye.reactive:mutiny:${rootProject.extra["mutiny-version"]}")
}

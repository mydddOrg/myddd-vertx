plugins {
    java
    kotlin("jvm")
}

group = "cc.lingenliu"
version = rootProject.extra["version"]!!

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    api("com.google.inject:guice:5.0.0-BETA-1")
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
}

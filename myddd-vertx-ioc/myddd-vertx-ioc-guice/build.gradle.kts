plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

extra["publishJar"] = true

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation("org.junit.jupiter:junit-jupiter:${rootProject.extra["junit5_version"]}")
    api("com.google.inject:guice:5.0.1")

    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
}

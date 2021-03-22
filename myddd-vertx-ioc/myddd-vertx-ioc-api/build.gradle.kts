plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!


tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}


dependencies {
    implementation(kotlin("stdlib"))
    api("javax.inject:javax.inject:1")

    testImplementation("org.junit.jupiter:junit-jupiter:${rootProject.extra["junit5_version"]}")
    testImplementation("org.mockito:mockito-core:3.7.7")
}

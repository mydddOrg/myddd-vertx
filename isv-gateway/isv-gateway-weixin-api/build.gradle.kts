plugins {
    java
}

group = "org.myddd.vertx"
version = "1.0.0-SNAPSHOT"

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}

dependencies {
    implementation("commons-codec:commons-codec:1.15")
    implementation("org.json:json:20210307")
    testImplementation("org.junit.jupiter:junit-jupiter:${rootProject.extra["junit5_version"]}")
}

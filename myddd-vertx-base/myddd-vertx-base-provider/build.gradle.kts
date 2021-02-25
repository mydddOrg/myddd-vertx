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

    api(project(":myddd-vertx-base:myddd-vertx-base-api"))

    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")

    //test
    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation("org.mockito:mockito-core:${rootProject.extra["mockito_version"]}")
}

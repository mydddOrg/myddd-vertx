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

    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-config:${rootProject.extra["vertx_version"]}")

    implementation(project(":myddd-vertx-i18n:myddd-vertx-i18n-api"))

    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("org.mockito:mockito-core:3.7.7")
    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")

}

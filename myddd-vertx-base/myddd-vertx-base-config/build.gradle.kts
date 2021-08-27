plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

extra["publishJar"] = true

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

    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")

    api("io.vertx:vertx-config:${rootProject.extra["vertx_version"]}")


    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")

    //test
    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation("org.mockito:mockito-core:${rootProject.extra["mockito_version"]}")

}

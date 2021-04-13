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
    //base
    implementation(kotlin("stdlib"))
    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")


    //api
    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
    implementation(project(":myddd-vertx-cache:myddd-vertx-cache-api"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))

    //test
    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation("org.mockito:mockito-core:${rootProject.extra["mockito_version"]}")
    testImplementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))
}
plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx.example"
version = rootProject.extra["version"]!!

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}

dependencies {
    //base
    implementation(kotlin("stdlib"))
    api("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")

    //other layer
    implementation(project(":example:document-domain"))

    //api
    api(project(":myddd-vertx-repository:myddd-vertx-repository-api"))
    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))

    //api implementation
    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-hibernate"))
    implementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))

    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${rootProject.extra["jackson_version"]}")


    //test
    testImplementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))
    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")


}

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

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":myddd-vertx-domain"))
    api(project(":myddd-vertx-repository:myddd-vertx-repository-api"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))

    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")
    implementation(project(":myddd-vertx-base:myddd-vertx-base-api"))

    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.hibernate:hibernate-core:${rootProject.extra["hibernate_core_version"]}")

    api("io.smallrye.reactive:mutiny:0.14.0")
    api("com.ibm.async:asyncutil:0.1.0")
    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")

    testImplementation("javax.inject:javax.inject:1")
    testImplementation("org.eclipse.persistence:javax.persistence:${rootProject.extra["javax_persistence_version"]}")
    testImplementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))
    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))
    testImplementation("io.vertx:vertx-pg-client:${rootProject.extra["vertx_version"]}")
    testImplementation("io.vertx:vertx-mysql-client:${rootProject.extra["vertx_version"]}")

}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
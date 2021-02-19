plugins {
    java
    kotlin("jvm")
}

group = "org.myddd.vertx"
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
    implementation(kotlin("stdlib"))

    implementation(project(":myddd-vertx-querychannel:myddd-vertx-querychannel-api"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    testImplementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))

    implementation("io.vertx:vertx-mysql-client:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")
    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.hibernate:hibernate-core:5.4.27.Final")

    testImplementation("javax.inject:javax.inject:1")
    testImplementation("org.eclipse.persistence:javax.persistence:${rootProject.extra["javax_persistence_version"]}")

    implementation("io.smallrye.reactive:mutiny:0.13.0")
    implementation("com.ibm.async:asyncutil:0.1.0")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    testImplementation(project(":myddd-vertx-domain"))
    testImplementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))
    testImplementation(project(":myddd-vertx-repository:myddd-vertx-repository-hibernate"))

}

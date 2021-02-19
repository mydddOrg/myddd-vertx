import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    java
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "6.1.0"
}


group = "org.myddd.vertx"
version = rootProject.extra["version"]!!

val mainVerticleName = "org.myddd.vertx.oauth2.start.MydddOAuth2Verticle"
val launcherClassName = "io.vertx.core.Launcher"


tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}

application {
    mainClassName = launcherClassName
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("fat")
    manifest {
        attributes(mapOf("Main-Verticle" to mainVerticleName))
    }
    mergeServiceFiles()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")

    implementation("io.vertx:vertx-web:${rootProject.extra["vertx_version"]}")

    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))

    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-api"))
    implementation(project(":myddd-vertx-repository:myddd-vertx-repository-hibernate"))

    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-domain"))
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-api"))
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-application"))
    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-infra"))

    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")

    implementation("javax.inject:javax.inject:1")
    implementation("org.eclipse.persistence:javax.persistence:${extra["javax_persistence_version"]}")

    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation("io.vertx:vertx-web-client:${rootProject.extra["vertx_version"]}")

    implementation("com.fasterxml.jackson.core:jackson-databind:${extra["jackson_version"]}")


}

plugins {
    java
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.foreverht.isvgateway"
version = rootProject.extra["isv_gateway_version"]!!


tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}

val mainVerticleName = "com.foreverht.isvgateway.bootstrap.ISVBootstrapVerticle"
val launcherClassName = "io.vertx.core.Launcher"

application {
    mainClassName = launcherClassName
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("fat")
    manifest {
        attributes(mapOf("Main-Verticle" to mainVerticleName))
    }
    mergeServiceFiles()
}

dependencies {
    implementation(kotlin("stdlib"))

    api("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-web-client:${rootProject.extra["vertx_version"]}")

    implementation(project(":myddd-vertx-web"))
    implementation(project(":isv-gateway:isv-gateway-domain"))
    implementation(project(":isv-gateway:isv-gateway-api"))
    implementation(project(":isv-gateway:isv-gateway-application"))
    implementation(project(":isv-gateway:isv-gateway-infra"))



    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    api(project(":myddd-vertx-i18n:myddd-vertx-i18n-api"))
    api(project(":myddd-vertx-repository:myddd-vertx-repository-api"))

    implementation(project(":myddd-vertx-oauth2:myddd-vertx-oauth2-api"))


    implementation("io.vertx:vertx-json-schema:${rootProject.extra["vertx_version"]}")
    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${rootProject.extra["jackson_version"]}")


    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")

}

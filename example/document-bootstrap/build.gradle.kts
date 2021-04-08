plugins {
    java
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "6.1.0"
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

val mainVerticleName = "cc.lingenliu.example.document.bootstrap.DocumentBootstrapVerticle"
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
    implementation(project(":example:document-domain"))
    implementation(project(":example:document-api"))
    implementation(project(":example:document-application"))
    implementation(project(":example:document-infra"))

    implementation(project(":myddd-vertx-media:myddd-vertx-media-domain"))
    implementation(project(":myddd-vertx-media:myddd-vertx-media-infra"))
    implementation(project(":myddd-vertx-media:myddd-vertx-media-storage:myddd-vertx-media-storage-qcloud"))

    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    api(project(":myddd-vertx-i18n:myddd-vertx-i18n-api"))
    api(project(":myddd-vertx-repository:myddd-vertx-repository-api"))


    implementation(project(":myddd-vertx-i18n:myddd-vertx-i18n-provider"))
    implementation(project(":myddd-vertx-base:myddd-vertx-base-provider"))

    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))


    implementation("io.vertx:vertx-json-schema:${rootProject.extra["vertx_version"]}")
    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${rootProject.extra["jackson_version"]}")
    implementation("com.fasterxml:aalto-xml:1.2.2")

    //weixin api dependencry
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("commons-codec:commons-codec:1.15")


    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")

}

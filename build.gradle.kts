import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//使用新的kotlin compile
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.useIR = true

plugins {
    java
    kotlin("jvm") version "1.4.31"
    id("jacoco")
    id("org.sonarqube") version "3.0"
}

extra["version"] = "1.0.0-SNAPSHOT"
extra["vertx_version"] = "4.0.3"

extra["log4j_version"] = "2.14.0"
extra["jackson_version"] = "2.12.1"
extra["javax_persistence_version"] = "2.2.1"
extra["mockito_version"] = "3.7.7"
extra["hibernate_core_version"] = "5.4.28.Final"
extra["commons_lang3_version"] = "3.12.0"

extra["isv_gateway_version"] = "1.0.0-SNAPSHOT"

group = "org.myddd.vertx"
version = project.extra["version"]!!

subprojects {
    apply(plugin = "jacoco")
    apply(plugin = "org.sonarqube")
}


sonarqube {
    properties {
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.sources", "src")
        property("sonar.language","kotlin")
        property("sonar.sources","src/main/kotlin")
    }
}

allprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}

repositories {
    mavenCentral()
}


dependencies {
    implementation(kotlin("stdlib"))
}

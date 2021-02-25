import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.21"
    id("jacoco")
    id("org.sonarqube") version "3.0"
}

extra["version"] = "1.0-SNAPSHOT"
extra["vertx_version"] = "4.0.2"

extra["log4j_version"] = "2.14.0"
extra["jackson_version"] = "2.12.1"
extra["javax_persistence_version"] = "2.2.1"
extra["mockito_version"] = "3.7.7"

group = "cc.lingenliu"
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
        maven {
            setUrl("https://maven.aliyun.com/repository/public/")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/spring/")
        }
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

repositories {
    mavenCentral()
}


dependencies {
    implementation(kotlin("stdlib"))
}

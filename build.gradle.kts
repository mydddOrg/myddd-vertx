import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.21"
}

extra["version"] = "1.0-SNAPSHOT"
extra["vertx_version"] = "4.0.0"

group = "cc.lingenliu"
version = project.extra["version"]!!

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

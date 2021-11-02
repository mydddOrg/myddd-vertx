import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
buildscript {
    extra["kotlinVersion"] = "1.5.31"
    val kotlinVersion: String by extra

    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.17")
    }
}


plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.5.31"
    id("jacoco")
    id("org.sonarqube") version "3.0"
}

val projectVersion = "1.2.1-SNAPSHOT"

extra["version"] = projectVersion

extra["kotlin_version"] = "1.5.31"
extra["vertx_version"] = "4.1.5"
extra["hibernate_reactive_version"] = "1.0.0.Final"

extra["log4j_version"] = "2.14.0"
extra["jackson_version"] = "2.12.1"
extra["javax_persistence_version"] = "2.2.1"
extra["mockito_version"] = "4.0.0"
extra["commons_lang3_version"] = "3.12.0"
extra["junit5_version"] = "5.7.1"

extra["protobuf-java"] = "3.17.3"
extra["annotation-api"] = "1.3.2"

group = "org.myddd.vertx"
version = projectVersion



subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "org.sonarqube")

    jacoco {
        toolVersion = "0.8.7"
    }

    tasks.jacocoTestReport {
        reports {
            xml.required.set(true)
            csv.required.set(false)
            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    afterEvaluate {
        val publishJar = this.extra.has("publishJar")
        if(publishJar){
            apply(plugin = "maven-publish")

            publishing {

                publications {

                    create<MavenPublication>("mavenJava"){
                        groupId = "org.myddd.vertx"
                        afterEvaluate {
                            artifactId = tasks.jar.get().archiveBaseName.get()
                        }
                        from(components["java"])
                    }

                    repositories {
                        maven {

                            val releasesRepoUrl = "sftp://ssh.myddd.org:10010/repository/releases"
                            val snapshotsRepoUrl = "sftp://ssh.myddd.org:10010/repository/snapshots"
                            version = projectVersion
                            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

                            credentials {
                                username = if(project.hasProperty("username")) project.property("username") as String? else ""
                                password = if(project.hasProperty("password")) project.property("password") as String? else ""
                            }

                        }
                    }
                }

            }
        }
    }



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
            setUrl("https://maven.myddd.org/releases/")
        }
        maven {
            setUrl("https://maven.myddd.org/snapshots/")
        }

        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }



}

repositories {
    maven {
        setUrl("https://maven.myddd.org/releases/")
    }
    maven {
        setUrl("https://maven.myddd.org/snapshots/")
    }

    mavenCentral()
}

tasks.jar {
    enabled = true
}

dependencies {
    implementation(kotlin("stdlib"))
}

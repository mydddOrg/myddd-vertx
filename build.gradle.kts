import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.5.0"
    id("jacoco")
    id("org.sonarqube") version "3.0"
}

val projectVersion = "1.0.1-SNAPSHOT"

extra["version"] = projectVersion
extra["vertx_version"] = "4.0.3"

extra["log4j_version"] = "2.14.0"
extra["jackson_version"] = "2.12.1"
extra["javax_persistence_version"] = "2.2.1"
extra["mockito_version"] = "3.7.7"
extra["hibernate_core_version"] = "5.4.28.Final"
extra["commons_lang3_version"] = "3.12.0"
extra["junit5_version"] = "5.7.1"

group = "org.myddd.vertx"
version = project.extra["version"]!!

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "org.sonarqube")
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

        maven {
            setUrl("https://maven.aliyun.com/repository/public/")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/spring/")
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

    maven {
        setUrl("https://maven.aliyun.com/repository/public/")
    }
    maven {
        setUrl("https://maven.aliyun.com/repository/spring/")
    }

    mavenCentral()
}


tasks.jar {
    enabled = true
}

dependencies {
    implementation(kotlin("stdlib"))
}

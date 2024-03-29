import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
buildscript {
    extra["kotlinVersion"] = "1.7.0"

    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.19")
    }
}


plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.8.10"
    id("jacoco")
    id("org.sonarqube") version "3.4.0.2513"
}

val projectVersion = "0.3.6-SNAPSHOT"

extra["version"] = projectVersion

extra["kotlin_version"] = "1.8.10"
extra["vertx_version"] = "4.4.1"
extra["hibernate_reactive_version"] = "1.1.9.Final"

extra["log4j_version"] = "2.20.0"
extra["jackson_version"] = "2.14.2"
extra["javax_persistence_version"] = "2.2.1"
extra["mockito_version"] = "5.2.0"
extra["commons_lang3_version"] = "3.12.0"
extra["junit5_version"] = "5.8.2"

extra["protobuf-java"] = "3.17.3"
extra["annotation-api"] = "1.3.2"
extra["assertj-core-version"] = "3.21.0"
extra["protoc-gen-version"] = "1.44.1"

extra["mutiny-version"] = "1.6.0"

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

    tasks.test {
        finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test) // tests are required to run before generating the report
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

                            val releasesRepoUrl = "sftp://ssh.myddd.org:10010/repositories/releases"
                            val snapshotsRepoUrl = "sftp://ssh.myddd.org:10010/repositories/snapshots"
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

        if(!this.sonarqube.isSkipProject){
            tasks.check {
                finalizedBy(tasks.jacocoTestCoverageVerification) // report is always generated after tests run
            }

            tasks.jacocoTestCoverageVerification {
                violationRules {
                    rule {
                        limit {
                            minimum = "0.8".toBigDecimal()
                        }
                    }
                }
            }
        }
    }



    dependencies{

        implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")

        implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
        implementation("io.vertx:vertx-lang-kotlin:${rootProject.extra["vertx_version"]}")
        implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")
        testImplementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))
        testImplementation(project(":myddd-vertx-junit"))
        testImplementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
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

        maven {
            setUrl("https://jitpack.io")
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

}

tasks.jar {
    enabled = true
}
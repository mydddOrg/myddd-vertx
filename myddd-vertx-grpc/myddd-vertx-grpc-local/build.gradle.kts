import com.google.protobuf.gradle.*

plugins {
    java
    kotlin("jvm")
    id("com.google.protobuf")
}

group = "org.myddd.vertx"
version = rootProject.extra["version"]!!
val mySkipTests = true
extra["publishJar"] = true

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":myddd-vertx-domain"))

    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")

    implementation("com.google.protobuf:protobuf-java:${rootProject.extra["protobuf"]}")
    implementation("io.vertx:vertx-grpc:${rootProject.extra["vertx_version"]}")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation("org.junit.jupiter:junit-jupiter:${rootProject.extra["junit5_version"]}")
    testImplementation("org.mockito:mockito-core:3.7.7")
}

sourceSets.main {
    proto.srcDir("src/main/protobuf")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${rootProject.extra["protobuf"]}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.39.0"
        }

        id("vertx") {
            artifact = "io.vertx:vertx-grpc-protoc-plugin:${rootProject.extra["vertx_version"]}"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("vertx")
            }
        }
    }
}
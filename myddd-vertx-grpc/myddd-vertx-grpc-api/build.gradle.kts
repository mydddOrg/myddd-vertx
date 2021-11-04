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

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.vertx:vertx-grpc:${rootProject.extra["vertx_version"]}")
    api(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))


    implementation("javax.annotation:javax.annotation-api:${rootProject.extra["annotation-api"]}")
    implementation("com.google.protobuf:protobuf-java:${rootProject.extra["protobuf-java"]}")

    testImplementation(project(":myddd-vertx-grpc:myddd-vertx-grpc-provider"))
}

sourceSets.main {
    proto.srcDir("src/main/protobuf")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${rootProject.extra["protobuf-java"]}"
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
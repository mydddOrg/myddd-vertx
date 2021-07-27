plugins {
    java
    kotlin("jvm")
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
    implementation("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")


    api(project(":myddd-vertx-grpc:myddd-vertx-grpc-api"))
//    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:${rootProject.extra["kotlin_version"]}")

    implementation("com.google.protobuf:protobuf-java:${rootProject.extra["protobuf-java"]}")
    implementation("io.vertx:vertx-grpc:${rootProject.extra["vertx_version"]}")
    implementation("javax.annotation:javax.annotation-api:${rootProject.extra["annotation-api"]}")


    implementation("io.vertx:vertx-service-discovery:${rootProject.extra["vertx_version"]}")

    implementation("org.apache.logging.log4j:log4j-core:${rootProject.extra["log4j_version"]}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${rootProject.extra["jackson_version"]}")

    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation("org.junit.jupiter:junit-jupiter:${rootProject.extra["junit5_version"]}")
    testImplementation("org.mockito:mockito-core:3.7.7")

}

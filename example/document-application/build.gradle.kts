plugins {
    java
    kotlin("jvm")
}

group = "com.foreverht.isvgateway"
version = rootProject.extra["isv_gateway_version"]!!

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    api("io.vertx:vertx-core:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-lang-kotlin:${rootProject.extra["vertx_version"]}")
    api("io.vertx:vertx-lang-kotlin-coroutines:${rootProject.extra["vertx_version"]}")
    implementation("io.vertx:vertx-web-client:${rootProject.extra["vertx_version"]}")

    api(project(":example:document-domain"))
    api(project(":example:document-api"))

    implementation(project(":myddd-vertx-media:myddd-vertx-media-domain"))

    //查询通道
    api(project(":myddd-vertx-querychannel:myddd-vertx-querychannel-api"))
    //查询通道实现
    api(project(":myddd-vertx-querychannel:myddd-vertx-querychannel-hibernate"))

    implementation("commons-codec:commons-codec:1.15")

    //依赖注入
    implementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-api"))
    //依赖注入实现（仅测试下）
    testImplementation(project(":myddd-vertx-ioc:myddd-vertx-ioc-guice"))

    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:${rootProject.extra["jackson_version"]}")
    testImplementation("io.vertx:vertx-junit5:${rootProject.extra["vertx_version"]}")
    testImplementation(project(":example:document-infra"))

    testImplementation(project(":myddd-vertx-media:myddd-vertx-media-infra"))
    testImplementation(project(":myddd-vertx-media:myddd-vertx-media-storage:myddd-vertx-media-storage-qcloud"))

}

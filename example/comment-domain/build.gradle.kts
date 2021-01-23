plugins {
    java
    kotlin("jvm")
}

group = "cc.lingenliu"
version = rootProject.extra["version"]!!

dependencies {
    implementation(kotlin("stdlib"))

    //添加领域层的依赖
    api(project(":myddd-vertx-domain"))

}

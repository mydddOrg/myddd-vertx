plugins {
    java
    kotlin("jvm")
}

group = "cc.lingenliu"
version = rootProject.extra["version"]!!


dependencies {
    implementation(kotlin("stdlib"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
}

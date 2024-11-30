plugins {
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "8.3.0"
    application
}

val appVersion: String by project

group = "cn.rtast"
version = appVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.6")
    implementation("org.java-websocket:Java-WebSocket:1.5.7")
}

application {
    mainClass = "cn.rtast.kwsify.KwsifyKt"
}

tasks.shadowJar {
    dependsOn(tasks.build)
}
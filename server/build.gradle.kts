import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadow)
    id("application")
}

dependencies {
    implementation(project(":common"))
    implementation(libs.gson)
    implementation(libs.kotlinx.cli)
    implementation(libs.java.websocket)
}

application {
    mainClass = "cn.rtast.kwsify.MainKt"
}

tasks.shadowJar {
    enabled = false
}

tasks.register<ShadowJar>("buildShadowJar") {
    group = "build"
    description = "Manually build the shadow JAR file"
    archiveClassifier.set("all")
    from(sourceSets.main.get().output)
    configurations = listOf(project.configurations.runtimeClasspath.get())
    manifest {
        attributes(mapOf("Main-Class" to application.mainClass))
    }
}

tasks.named("shadowDistZip") {
    dependsOn("buildShadowJar")
}

tasks.named("shadowDistTar") {
    dependsOn("buildShadowJar")
}

tasks.named("startShadowScripts") {
    dependsOn("buildShadowJar")
}
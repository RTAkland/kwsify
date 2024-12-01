import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
    id("application")
    id("maven-publish")
}

val appVersion: String by project
val libVersion: String by project

group = "cn.rtast"
version = appVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.gson)
    implementation(libs.kotlinx.cli)
    implementation(libs.java.websocket)
}

application {
    mainClass = "cn.rtast.kwsify.KwsifyKt"
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

allprojects {
    apply {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "maven-publish")
    }

    repositories {
        mavenCentral()
    }

    val sourceJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    artifacts {
        archives(sourceJar)
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifact(sourceJar)
                artifactId = project.name
                version = libVersion
            }
        }

        repositories {
            maven {
                url = uri("https://repo.rtast.cn/api/v4/projects/49/packages/maven")
                credentials {
                    username = "RTAkland"
                    password = System.getenv("GITLAB_TOKEN")
                }
            }
        }
    }
}
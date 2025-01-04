import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
                url = uri("https://maven.rtast.cn/releases")
                credentials {
                    username = "RTAkland"
                    password = System.getenv("PUBLISH_TOKEN")
                }
            }
        }
    }

    tasks.compileKotlin {
        compilerOptions.jvmTarget = JvmTarget.JVM_1_8
    }

    tasks.compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
}
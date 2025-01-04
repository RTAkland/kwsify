import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin)
    id("maven-publish")
}

val appVersion: String by project
val libVersion: String by project

allprojects {
    group = "cn.rtast"
    version = appVersion

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "maven-publish")
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
                groupId = "cn.rtast"
                artifactId = "kwsify-${project.name}"
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
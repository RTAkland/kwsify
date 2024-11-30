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
val libVersion: String by project

group = "cn.rtast.kwsify"
version = libVersion

repositories {
    mavenCentral()
}

dependencies {
    api(libs.gson)
    api(libs.java.websocket)
    api(rootProject)
}

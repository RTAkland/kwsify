val libVersion: String by project

group = "cn.rtast.kwsify"
version = libVersion

repositories {
    mavenCentral()
}

dependencies {
    api(libs.java.websocket)
    api(project(":common"))
}

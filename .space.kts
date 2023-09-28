job("Build and run tests") {
    container("ubuntu:latest") {
        shellScript {
            content = "chmod +x ./gradlew"
        }
        shellScript {
            content = "./gradlew shadowJar"
        }
    }
}

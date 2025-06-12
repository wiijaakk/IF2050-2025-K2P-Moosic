plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.view"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.view.Register") // diubah untuk fitur Register
}

javafx {
    version = "24"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.github.cdimascio:dotenv-java:2.3.0") 
    implementation("org.postgresql:postgresql:42.7.3") 
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed") // 👈 ini yang bikin tampil
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

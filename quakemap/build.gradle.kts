plugins {
    kotlin("jvm")
    id("org.openjfx.javafxplugin") version "0.1.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
}

kotlin {
    jvmToolchain(21)
}

javafx {
    version = "22.0.1"
    modules = listOf("javafx.controls")
}

application {
    mainClass = "org.efford.quakes.MainKt"
}

tasks.named<JavaExec>("run") {
    args = listOf("4.5", "week")
}

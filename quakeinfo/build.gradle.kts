plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.dokka") version "1.9.20"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
    implementation("com.github.ajalt.clikt:clikt:4.4.0")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "org.efford.quakes.MainKt"
}

tasks.named<JavaExec>("run") {
    args = listOf("--summary", "--table", "--order", "-mag", "4.5", "day")
}

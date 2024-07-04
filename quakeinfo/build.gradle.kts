plugins {
    kotlin("jvm")
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
    implementation("com.github.ajalt.clikt:clikt:4.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "org.efford.quakes.MainKt"
}

tasks.named<JavaExec>("run") {
    args = listOf("-s", "-d", "--plain", "--order", "-mag", "4.5", "day")
}

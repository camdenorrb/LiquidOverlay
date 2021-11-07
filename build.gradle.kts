import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.jetbrains.compose") version "1.0.0-beta5"

}

group = "dev.twelveoclock"
version = "1.0.0"


repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}


val ktorVersion = "1.6.5"

dependencies {

    // Kotlin
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))

    // HTTP
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // GUI
    implementation(compose.desktop.currentOs)

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    // Junit 5
}


compose.desktop {
    application {
        mainClass = "dev.twelveoclock.liquidoverlay.Main"
    }
}

tasks {

    val javaVersion = JavaVersion.VERSION_16.toString()

    withType<KotlinCompile> {

        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        kotlinOptions.jvmTarget = javaVersion

        //kotlinOptions.freeCompilerArgs += listOf("suppressKotlinVersionCompatibilityCheck=true")
    }

    withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

}
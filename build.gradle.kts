import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //application
    kotlin("jvm") version "1.5.31"
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

dependencies {

    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))

    implementation(compose.desktop.currentOs)
}

/*
application {
    mainClass.set("dev.twelveoclock.liquidoverlay.Main")
}
*/

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

    compose {
        desktop {

        }
        //this.
    }
}
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.jetbrains.compose") version "1.0.0-beta6-dev446"
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

    // Sub Modules
    implementation(project(":NativeOverlay"))

    // HTTP
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    //implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-java:$ktorVersion")


    // GUI
    implementation(compose.desktop.currentOs)

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
}

tasks {

    val javaVersionCompat = JavaVersion.VERSION_16.toString()
    val javaVersion = JavaVersion.VERSION_17.toString()

    withType<KotlinCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        kotlinOptions.jvmTarget = javaVersionCompat
    }

    withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test framework
            useKotlinTest()
        }
    }
}

compose.desktop {
    application {
        mainClass = "dev.twelveoclock.liquidoverlay.MainKt"
    }
}

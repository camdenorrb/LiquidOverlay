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
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    // Sub Modules
    implementation(project(":NativeOverlay"))

    // GUI
    implementation(compose.desktop.currentOs)

}

tasks {

    //compileKotlin.get().destinationDirectory.set(compileJava.get().destinationDirectory.get())
    //compileTestKotlin.get().destinationDirectory.set(compileTestJava.get().destinationDirectory.get())

    val javaVersionCompat = JavaVersion.VERSION_17.toString()
    val javaVersion = JavaVersion.VERSION_17.toString()

    withType<KotlinCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        kotlinOptions.jvmTarget = javaVersionCompat
    }

    withType<JavaCompile> {
        //modularity.inferModulePath.set(true)
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
        //nativeDistributions.modules("PoderTech.overlay")
        println(nativeDistributions.modules)
        //nativeDistributions.includeAllModules = true
        jvmArgs("--list-modules", "--add-modules=jdk.incubator.foreign", "--enable-native-access=PoderTech.overlay", "--enable-native-access=ALL-UNNAMED")
        mainClass = "dev.twelveoclock.liquidoverlay.Main"
        //javaHome = System.getenv("JAVA_HOME")
    }
}

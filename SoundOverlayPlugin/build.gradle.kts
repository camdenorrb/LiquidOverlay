import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    //id("org.jetbrains.compose") version "1.0.0-beta5"
}

group = "dev.twelveoclock"
version = "1.0.0"

repositories {
    mavenCentral()
    //maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {

    // Kotlin
    compileOnly(platform(kotlin("bom")))
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("reflect"))

    // Sub Modules
    implementation(project(":SoftwareOverlay"))
    implementation(project(":NativeOverlay"))
}

tasks {

    //compileKotlin.get().destinationDirectory.set(compileJava.get().destinationDirectory.get())
    //compileTestKotlin.get().destinationDirectory.set(compileTestJava.get().destinationDirectory.get())

    val javaVersionCompat = JavaVersion.VERSION_16.toString()
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


    /*register("writePlugin") {
        dependsOn("clean", "shadowJar")
        val pluginsFolder = File("../SoftwareOverlay/Plugins")
        shadowJar.get().archiveFile.get().asFile.copyTo(pluginsFolder.resolve(shadowJar.get().archiveFileName.get()), true)
    }*/

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

/*

compose.desktop {
    application {
        //nativeDistributions.modules("PoderTech.overlay")
        //println(nativeDistributions.modules)
        //nativeDistributions.includeAllModules = true
        jvmArgs("--list-modules", "--add-modules=jdk.incubator.foreign", "--enable-native-access=PoderTech.overlay"/*, "--enable-native-access=ALL-UNNAMED"*/)
        mainClass = "dev.twelveoclock.liquidoverlay.Main"
        //javaHome = System.getenv("JAVA_HOME")
    }
}
*/
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm")
}

group = "tech.poder"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:atomicfu:0.18.3")
    testImplementation("org.junit.platform:junit-platform-commons:1.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(kotlin("test"))
}

tasks {

    compileKotlin.get().destinationDirectory.set(compileJava.get().destinationDirectory.get())
    compileTestKotlin.get().destinationDirectory.set(compileTestJava.get().destinationDirectory.get())

    val javaVersion = JavaVersion.VERSION_17.toString()

    withType<KotlinCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        kotlinOptions.jvmTarget = javaVersion
        //kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    withType<JavaCompile> {
        //modularity.inferModulePath.set(true)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    withType<Test> {
        useJUnitPlatform()
        jvmArgs("--add-modules=jdk.incubator.foreign", "--enable-native-access=PoderTech.overlay")
    }

    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    jar {
        dependsOn(sourcesJar)
    }

    artifacts {
        archives(sourcesJar)
        archives(jar)
    }
}
plugins {
    kotlin("jvm") version "1.6.0-RC2"
}

group = "tech.poder"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.platform:junit-platform-commons:+")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(kotlin("test"))
}

tasks {
    compileKotlin.get().destinationDirectory.set(compileJava.get().destinationDirectory.get())
    compileTestKotlin.get().destinationDirectory.set(compileTestJava.get().destinationDirectory.get())

    val javaVersion = JavaVersion.VERSION_17.toString()

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        kotlinOptions.jvmTarget = javaVersion
        //kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    withType<JavaCompile> {
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
plugins {
    val kotlinVersion = "1.9.21"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
}

group = "cloud.drakon"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "2.3.6"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

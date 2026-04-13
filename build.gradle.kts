plugins {
    kotlin("jvm") version "2.3.0"
    application
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("stakeholder.MainKt")
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.4")
}

tasks.test {
    useJUnitPlatform()
}

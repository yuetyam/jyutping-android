plugins {
        application
        kotlin("jvm") version "2.2.20"
}

group = "org.jyutping.preparing"
version = "0.1.0"

repositories {
        mavenCentral()
}

dependencies {
        implementation("org.xerial:sqlite-jdbc:3.50.3.0")
        implementation("org.slf4j:slf4j-simple:2.0.17")
        testImplementation(kotlin("test"))
}

tasks.test {
        useJUnitPlatform()
}

kotlin {
        jvmToolchain(21)
}

application {
        mainClass.set("org.jyutping.preparing.MainKt")
}

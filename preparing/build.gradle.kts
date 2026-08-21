plugins {
        application
        alias(libs.plugins.kotlin.jvm)
}

group = "org.jyutping.preparing"
version = "0.1.0"

repositories {
        mavenCentral()
}

dependencies {
        implementation(libs.sqlite.jdbc)
        implementation(libs.slf4j.simple)
        testImplementation(libs.kotlin.test)
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

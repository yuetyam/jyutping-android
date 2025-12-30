plugins {
        id("com.android.application")
        id("org.jetbrains.kotlin.android")
        id("org.jetbrains.kotlin.plugin.compose")
        id("org.jetbrains.kotlin.plugin.serialization")
}

android {
        namespace = "org.jyutping.jyutping"
        compileSdk = 36
        defaultConfig {
                applicationId = "org.jyutping.jyutping"
                minSdk = 29
                targetSdk = 36
                versionCode = 49
                versionName = "0.49.0"
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                vectorDrawables.useSupportLibrary = true
        }
        buildTypes {
                debug {
                        isMinifyEnabled = false
                }
                release {
                        isMinifyEnabled = true
                        isShrinkResources = true
                        proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"),
                                "proguard-rules.pro"
                        )
                }
        }
        compileOptions {
                sourceCompatibility = JavaVersion.VERSION_21
                targetCompatibility = JavaVersion.VERSION_21
        }
        kotlin {
                jvmToolchain(21)
        }
        buildFeatures {
                compose = true
                buildConfig = true
        }
        packaging {
                resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
}

java {
        toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
        }
}

val composeVersion = "1.10.0"
dependencies {
        implementation("androidx.activity:activity-compose:1.12.2")
        implementation("androidx.activity:activity-ktx:1.12.2")
        implementation("androidx.compose.material:material-icons-extended:1.7.8")
        implementation("androidx.compose.material3:material3:1.4.0")
        implementation("androidx.compose.ui:ui:$composeVersion")
        implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
        implementation("androidx.core:core-ktx:1.17.0")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
        implementation("androidx.lifecycle:lifecycle-service:2.10.0")
        implementation("androidx.navigation:navigation-compose:2.9.6")
        implementation("com.louiscad.splitties:splitties-systemservices:3.0.0")
        implementation("com.louiscad.splitties:splitties-views:3.0.0")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
        testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.1")
        androidTestImplementation("androidx.test.ext:junit:1.3.0")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
        androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
        debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
        debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
}

tasks.withType<Test>().configureEach {
        enabled = false
}

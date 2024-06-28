plugins {
        id("com.android.application")
        id("org.jetbrains.kotlin.android")
        id("org.jetbrains.kotlin.plugin.compose")
        id("org.jetbrains.kotlin.plugin.serialization")
}

android {
        namespace = "org.jyutping.jyutping"
        compileSdk = 34
        defaultConfig {
                applicationId = "org.jyutping.jyutping"
                minSdk = 33
                targetSdk = 34
                versionCode = 4
                versionName = "0.4.0"
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                vectorDrawables {
                        useSupportLibrary = true
                }
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
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
        }
        kotlinOptions {
                jvmTarget = JavaVersion.VERSION_17.toString()
        }
        buildFeatures {
                compose = true
                buildConfig = true
        }
        composeOptions {
                kotlinCompilerExtensionVersion = "1.5.13"
        }
        packaging {
                resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
        }
}

val composeVersion = "1.6.8"
dependencies {
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
        implementation("androidx.lifecycle:lifecycle-service:2.8.2")
        implementation("androidx.activity:activity-compose:1.9.0")
        implementation("androidx.compose.ui:ui:$composeVersion")
        implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
        implementation("androidx.compose.material:material-icons-extended:$composeVersion")
        implementation("androidx.compose.material3:material3:1.2.1")
        implementation("androidx.navigation:navigation-compose:2.7.7")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
        implementation("com.louiscad.splitties:splitties-systemservices:3.0.0")
        implementation("com.louiscad.splitties:splitties-views:3.0.0")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
        androidTestImplementation("androidx.test.ext:junit:1.2.1")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
        androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
        debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
        debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
}

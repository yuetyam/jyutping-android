plugins {
        id("com.android.application")
        id("org.jetbrains.kotlin.android")
        id("org.jetbrains.kotlin.plugin.compose")
        id("org.jetbrains.kotlin.plugin.serialization")
}

android {
        namespace = "org.jyutping.jyutping"
        compileSdk = 35
        defaultConfig {
                applicationId = "org.jyutping.jyutping"
                minSdk = 29
                targetSdk = 35
                versionCode = 28
                versionName = "0.28.0"
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
        /*
        composeOptions {
                kotlinCompilerExtensionVersion = "1.5.14"
        }
        */
        packaging {
                resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
}

val composeVersion = "1.7.6"
dependencies {
        implementation("androidx.activity:activity-compose:1.9.3")
        implementation("androidx.activity:activity-ktx:1.9.3")
        implementation("androidx.compose.material:material-icons-extended:$composeVersion")
        implementation("androidx.compose.material3:material3:1.3.1")
        implementation("androidx.compose.ui:ui:$composeVersion")
        implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
        implementation("androidx.core:core-ktx:1.15.0")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
        implementation("androidx.lifecycle:lifecycle-service:2.8.7")
        implementation("androidx.navigation:navigation-compose:2.8.5")
        implementation("com.louiscad.splitties:splitties-systemservices:3.0.0")
        implementation("com.louiscad.splitties:splitties-views:3.0.0")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.2")
        androidTestImplementation("androidx.test.ext:junit:1.2.1")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
        androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
        debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
        debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
}

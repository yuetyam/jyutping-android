plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.compose)
        alias(libs.plugins.kotlin.serialization)
}

val debugStoreFile = providers.gradleProperty("DEBUG_STORE_FILE")
    .orElse(providers.systemProperty("user.home").map { "$it/.android/debug.keystore" })
val debugStorePassword = providers.gradleProperty("DEBUG_STORE_CODE").orElse("android")
val debugKeyAlias = providers.gradleProperty("DEBUG_KEY_ALIAS").orElse("androiddebugkey")
val debugKeyPassword = providers.gradleProperty("DEBUG_KEY_CODE").orElse("android")
val hasCustomDebugStore = debugStoreFile.map { file(it).exists() }

android {
        namespace = "org.jyutping.jyutping"
        compileSdk = 37
        defaultConfig {
                applicationId = "org.jyutping.jyutping"
                minSdk = 33
                targetSdk = 37
                versionCode = 71
                versionName = "0.66.0"
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                vectorDrawables.useSupportLibrary = true
        }
        signingConfigs {
                if (hasCustomDebugStore.get()) {
                        create("CustomDebug") {
                                storeFile = file(debugStoreFile.get())
                                storePassword = debugStorePassword.get()
                                keyAlias = debugKeyAlias.get()
                                keyPassword = debugKeyPassword.get()
                        }
                }
        }
        buildTypes {
                debug {
                        isMinifyEnabled = false
                        if (hasCustomDebugStore.get()) {
                                signingConfig = signingConfigs.getByName("CustomDebug")
                        }
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
        buildFeatures {
                compose = true
                buildConfig = true
        }
        compileOptions {
                sourceCompatibility = JavaVersion.VERSION_21
                targetCompatibility = JavaVersion.VERSION_21
        }
        androidResources {
                @Suppress("UnstableApiUsage")
                generateLocaleConfig = true
        }
}

java {
        toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
        }
}
kotlin {
        jvmToolchain(21)
}

dependencies {
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.activity.ktx)
        implementation(libs.androidx.compose.material)
        implementation(libs.androidx.compose.material.icons.extended)
        implementation(libs.androidx.compose.material3)
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.compose.ui.tooling.preview)
        implementation(libs.androidx.concurrent.futures)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.lifecycle.service)
        implementation(libs.androidx.navigation.compose)
        implementation(libs.google.material)
        implementation(libs.errorprone.annotations)
        implementation(libs.splitties.systemservices)
        implementation(libs.splitties.views)
        implementation(libs.kotlinx.serialization.json)
        testImplementation(libs.junit.jupiter.api)
        androidTestImplementation(libs.androidx.test.ext.junit)
        androidTestImplementation(libs.androidx.test.espresso.core)
        androidTestImplementation(libs.androidx.compose.ui.test.junit4)
        debugImplementation(libs.androidx.compose.ui.tooling)
        debugImplementation(libs.androidx.compose.ui.test.manifest)
}

tasks.withType<Test>().configureEach {
        enabled = false
}

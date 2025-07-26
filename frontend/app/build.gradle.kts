    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
    }

    android {
        namespace = "com.example.moodii"
        compileSdk = 36 // Good, using a recent SDK

        defaultConfig {
            applicationId = "com.example.moodii"
            minSdk = 24
            targetSdk = 36
            versionCode = 1
            versionName = "1.0"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.3" // This is compatible with Compose BOM 2024.06.00 and Kotlin 1.9.0, which you likely use
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        kotlinOptions {
            jvmTarget = "11"
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
    }

    dependencies {
        // Core KTX extensions
        implementation(libs.androidx.core.ktx)

        //Retrofit + gson dependencies
        implementation ("com.squareup.retrofit2:retrofit:2.9.0")
        implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

        // Jetpack Compose dependencies
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.ui) // Base UI components for Compose
        implementation(libs.androidx.ui.tooling.preview) // For @Preview composables
        implementation(libs.androidx.navigation.compose) // For navigation in Compose
        implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle for Compose
        implementation(libs.androidx.lifecycle.viewmodel.compose) // ViewModel support for Compose
        implementation(libs.kotlinx.coroutines.android) // Kotlin Coroutines for async operations
        implementation("androidx.appcompat:appcompat:1.6.1")

        // Jetpack Compose Material 3 (Using BOM to manage version)
        implementation ("androidx.compose.material3:material3:1.4.0-alpha17") // <--- CORRECTED LINE
        implementation ("androidx.compose.material:material-icons-extended:1.7.8") // No version needed with BOM
        implementation ("androidx.compose:compose-bom:2024.04.00") // This manages the versions
        implementation ("androidx.compose.ui:ui") // No version needed with BOM
        implementation ("androidx.compose.ui:ui-graphics") // No version needed with BOM
        implementation ("androidx.compose.animation:animation") // No version needed with BOM
        implementation ("androidx.compose.ui:ui-text") // No version needed with BOM

        // Debugging tools for Compose
        debugImplementation(libs.androidx.ui.tooling)

        implementation("com.squareup.okhttp3:okhttp:4.12.0") // Or the latest version
        implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Optional, for logging
        // Testing dependencies
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(libs.androidx.ui.test.junit4)
    }
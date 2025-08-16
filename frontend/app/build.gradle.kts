    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
    }

    android {
    namespace = "com.example.moodii"
    compileSdk = 34 // Align with stable SDK supported by AGP 8.1.1

        defaultConfig {
            applicationId = "com.example.moodii"
            minSdk = 24
            targetSdk = 34
            versionCode = 1
            versionName = "1.0"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            // Backend base URL (injected into BuildConfig)
            buildConfigField("String", "API_BASE_URL", "\"http://moodii-backend-1852.eastus.azurecontainer.io:8080\"")
        }

        // Optional product flavors for future (dev/prod). Currently prod mirrors default.
        flavorDimensions += listOf("env")
        productFlavors {
            create("dev") {
                dimension = "env"
                buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080\"") // Emulator loopback
            }
            create("prod") {
                dimension = "env"
                buildConfigField("String", "API_BASE_URL", "\"http://moodii-backend-1852.eastus.azurecontainer.io:8080\"")
            }
        }

        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.3" // This is compatible with Compose BOM 2024.06.00 and Kotlin 1.9.0, which you likely use
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        kotlinOptions {
            jvmTarget = "17"
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }

        signingConfigs {
            create("release") {
                // Values resolved from Gradle properties or environment variables; do NOT hardcode secrets
                val storeFilePath = (project.findProperty("RELEASE_STORE_FILE") as String?) ?: System.getenv("ANDROID_KEYSTORE_PATH")
                if (storeFilePath != null && file(storeFilePath).exists()) {
                    storeFile = file(storeFilePath)
                }
                storePassword = (project.findProperty("RELEASE_STORE_PASSWORD") as String?) ?: System.getenv("RELEASE_STORE_PASSWORD")
                keyAlias = (project.findProperty("RELEASE_KEY_ALIAS") as String?) ?: System.getenv("RELEASE_KEY_ALIAS")
                keyPassword = (project.findProperty("RELEASE_KEY_PASSWORD") as String?) ?: System.getenv("RELEASE_KEY_PASSWORD")
            }
        }

        buildTypes {
            getByName("debug") {
                // Optional extra logging/interceptors can be toggled here
                isMinifyEnabled = false
            }
            getByName("release") {
                isMinifyEnabled = true
                isShrinkResources = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                // Only apply signing if all credentials are available; otherwise fall back to default debug signing
                val hasKeystore = (project.findProperty("RELEASE_STORE_FILE") as String?)?.let { file(it).exists() } == true || System.getenv("ANDROID_KEYSTORE_PATH")?.let { file(it).exists() } == true
                val hasCreds = (project.findProperty("RELEASE_STORE_PASSWORD") ?: System.getenv("RELEASE_STORE_PASSWORD")) != null &&
                        (project.findProperty("RELEASE_KEY_ALIAS") ?: System.getenv("RELEASE_KEY_ALIAS")) != null &&
                        (project.findProperty("RELEASE_KEY_PASSWORD") ?: System.getenv("RELEASE_KEY_PASSWORD")) != null
                if (hasKeystore && hasCreds) {
                    println("[build.gradle] Using provided release keystore for signing.")
                    signingConfig = signingConfigs.getByName("release")
                } else {
                    println("[build.gradle] Release keystore credentials missing; building unsigned (will be signed with debug if installed). Provide RELEASE_* properties or env vars to enable real signing.")
                }
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

    // Compose BOM pins versions; list BOM first then modules without versions
    implementation(platform("androidx.compose:compose-bom:2024.04.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.ui:ui-text")

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
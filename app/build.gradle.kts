plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    // Plugin tambahan untuk Architecture
    alias(libs.plugins.ksp) // Wajib untuk Room Database (Pengganti KAPT)
    alias(libs.plugins.hilt) // Wajib untuk Dependency Injection
}

android {
    namespace = "com.example.smartretail"
    compileSdk = 34 // Kita gunakan SDK Stabil 34 (Android 14)

    defaultConfig {
        applicationId = "com.example.smartretail"
        minSdk = 24 // Sesuai SRS (Support Android 7.0+)
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            // SECURITY REQUIREMENT (SRS 5.3): Code Obfuscation
            // Mengaktifkan R8 untuk mengacak kode agar susah di-hack
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // URL untuk production
            buildConfigField("String", "BASE_URL", "\"https://api.your-production-domain.com/\"")
        }
        debug {
            // URL untuk development (emulator)
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/\"")
        }
    }

    // Update ke Java 17 agar kompatibel dengan Hilt & Android Studio terbaru
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // --- 1. Core Android & UI (Jetpack Compose) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended")
    
    // Accompanist for SwipeRefresh (Material 3 compatible)
    implementation("com.google.accompanist:accompanist-swiperefresh:0.32.0")
    
    // Vico Charts for revenue trends (Material 3 compatible)
    implementation("com.patrykandpatrick.vico:compose:1.13.1")
    implementation("com.patrykandpatrick.vico:compose-m3:1.13.1")
    implementation("com.patrykandpatrick.vico:core:1.13.1")

    // --- 2. Navigation Component (Pindah Layar) ---
    implementation(libs.androidx.navigation.compose)

    // --- 3. Room Database (OFFLINE-FIRST CORE) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Penting buat Coroutines support
    ksp(libs.androidx.room.compiler) // KSP Processor (Lebih cepat dari KAPT)

    // --- 4. Datastore ---
    implementation(libs.androidx.datastore.preferences)

    // --- 5. Networking / API (Retrofit & OkHttp) ---
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging) // Buat liat log request API di Logcat

    // --- 6. Dependency Injection (Hilt) ---
    // Ini bikin kodingan rapi (MVVM Pattern)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // --- 7. Background Sync (WorkManager) ---
    // Ini "Jantung" fitur auto-sync saat ada sinyal
    implementation(libs.androidx.work.runtime.ktx)

    // --- 8. Testing & Debugging ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
}
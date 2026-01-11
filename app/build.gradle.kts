plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)

}

android {
    namespace = "id.antasari.mentalityapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "id.antasari.mentalityapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // 1. UI & Navigation (Jetpack Compose)
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // 2. Icons (Extended Material Icons) - Untuk icon lucu
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    // 3. Networking (Ktor Client) - Untuk connect ke Backend nanti
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7") // Engine
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")

    // 4. System UI Controller (Untuk ubah warna status bar)
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.0")

    // --- ROOM DATABASE (Ingatan Lokal) ---
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation("com.google.code.gson:gson:2.10.1")

    ksp("androidx.room:room-compiler:$room_version")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // SDK Google AI (Gemini)
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Lifecycle Scope (buat jalanin AI di background)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
}

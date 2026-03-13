import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Read local.properties from the project root
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

android {
    namespace = "com.example.kitchenassistant"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.kitchenassistant"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inject as BuildConfig constants — available at runtime
        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"${localProperties["SUPABASE_URL"] ?: ""}\""
        )
        buildConfigField(
            "String",
            "SUPABASE_KEY",
            "\"${localProperties["SUPABASE_KEY"] ?: ""}\""
        )
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
    buildFeatures {
        compose = true
        buildConfig = true
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

    // Liquid glass lib
    implementation("io.github.kyant0:backdrop:1.0.6")
    implementation("io.github.kyant0:capsule:2.1.3")

    // Icons
    implementation("androidx.compose.material:material-icons-extended")

    // Supabase BOM — controls all module versions in sync
    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))
    implementation("io.github.jan-tennert.supabase:auth-kt") // auth only — no postgrest needed

    // Ktor Android engine — required by the Supabase SDK for HTTP
    implementation("io.ktor:ktor-client-android:3.1.3")

    // ViewModel + Compose lifecycle integration
    val lifecycle_version = "2.10.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:${lifecycle_version}")
}
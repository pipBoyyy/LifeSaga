plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.lifesaga"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lifesaga"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        // Эта версия совместима с последними библиотеками Compose и Kotlin
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Compose Bill of Materials (BOM) - управляет версиями всех библиотек Compose
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))

    // Основные зависимости
    implementation("androidx.core:core-ktx:1.13.1") // Обновлено
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3") // Обновлено
    implementation("androidx.activity:activity-compose:1.9.0") // Обновлено

    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Иконки для Material 3 (ОСТАВЛЯЕМ ТОЛЬКО ЭТУ)
    //implementation("androidx.compose.material3:material3-icons-extended:1.6.7")

    // Навигация
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")


    // Тестовые зависимости
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1") // Обновлено
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1") // Обновлено
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

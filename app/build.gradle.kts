// Убедитесь, что эти плагины есть и раскомментированы
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Если этого плагина нет, могут быть проблемы с компилятором Kotlin для Compose
    // id("org.jetbrains.kotlin.plugin.compose") // Этот плагин может быть не нужен с новыми версиями AGP и Kotlin
}

android {
    namespace = "com.example.lifesaga" // Убедитесь, что здесь ваш пакет
    compileSdk = 34 // Рекомендуется использовать актуальную версию, например 34

    defaultConfig {
        applicationId = "com.example.lifesaga"
        minSdk = 24
        targetSdk = 34 // Рекомендуется использовать актуальную версию
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
        sourceCompatibility = JavaVersion.VERSION_1_8 // Или JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_1_8 // Или JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "1.8" // Или "11"
    }
    // Эти опции КРИТИЧЕСКИ ВАЖНЫ для Jetpack Compose
    buildFeatures {
        compose = true // Включает поддержку Compose в сборке
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13" // Убедитесь, что версия совместима с вашей версией Kotlin
        // Актуальные версии можно найти на:
        // https://developer.android.com/jetpack/androidx/releases/compose-kotlin
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))

    // Основные зависимости AndroidX
    implementation("androidx.core:core-ktx:1.12.0") // Используйте актуальные версии
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2") // Зависимость для интеграции Activity с Compose
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3-icons-extended")
    implementation("androidx.navigation:navigation-compose")

// Или последняя стабильная версия
    // Зависимости Jetpack Compose UI
    // Это БАЗОВЫЕ зависимости для Compose. Убедитесь, что они есть.
      implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3") // Эта версия будет управляться BOM
    implementation("androidx.compose.material:material-icons-extended")
    // ...
    // Зависимости для тестирования (можно оставить как есть)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00")) // BOM для тестовых зависимостей Compose
    androidTestImplementation("androidx.compose.ui:ui-test-junit4") // Для UI-тестов Compose
    debugImplementation("androidx.compose.ui:ui-tooling") // Для инструментов разработки Compose (например, Layout Inspector)
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
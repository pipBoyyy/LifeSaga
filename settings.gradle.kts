// В файле settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()       // <-- Репозиторий Google (для библиотек AndroidX)
        mavenCentral() // <-- Центральный репозиторий Maven (для всего остального)
    }
}
rootProject.name = "LifeSaga"
include(":app")

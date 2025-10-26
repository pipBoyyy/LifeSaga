package com.example.lifesaga.navigation

sealed class Screen(val route: String) {
    // Экраны без аргументов
    data object Splash : Screen("splash_screen") // <- ДОБАВЬ ЭТУ СТРОКУ
    data object MainMenu : Screen("main_menu_screen")
    data object CharacterCreation : Screen("character_creation_screen")
    data object MainGame : Screen("main_game_screen")
    data object Settings : Screen("settings_screen")
    data object GameOver : Screen("game_over_screen") // <- ДОБАВЬ ЭТУ СТРОКУ
    data object Jobs : Screen("jobs_screen") // <- ДОБАВЬ ЭТУ СТРОКУ
}

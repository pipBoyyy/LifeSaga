package com.example.lifesaga.navigation

sealed class Screen(val route: String) {
    // Используй object, так как это экраны без аргументов
    // Важно, чтобы они наследовались от Screen
    data object MainMenu : Screen("main_menu_screen")
    data object CharacterCreation : Screen("character_creation_screen")
    data object MainGame : Screen("main_game_screen")
    data object Settings : Screen("settings_screen")
}

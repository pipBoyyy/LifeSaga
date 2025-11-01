// В файле navigation/Screen.kt
package com.example.lifesaga.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash") // <-- ДОБАВЬ ЭТУ СТРОКУ ОБРАТНО
    object MainMenu : Screen("main_menu")
    object CharacterCreation : Screen("character_creation")
    object MainGame : Screen("main_game")
    object Settings : Screen("settings")
    object GameOver : Screen("game_over")
    object Jobs : Screen("jobs")
    object School : Screen("school")
    object Assets : Screen("assets")
    object Relationships : Screen("relationships")
    object Actions : Screen("actions")// <-- ДОБАВЬ ЭТУ СТРОКУ
}

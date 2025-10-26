package com.example.lifesaga.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.lifesaga.ui.screens.CharacterCreationScreen
import com.example.lifesaga.ui.screens.MainGameScreen
import com.example.lifesaga.ui.screens.MainMenuScreen
import com.example.lifesaga.ui.screens.SettingsScreen
import com.example.lifesaga.viewmodel.MainGameViewModel
import com.example.lifesaga.navigation.Screen
// ... все твои импорты ...

@OptIn(androidx.navigation.NavGraph.Companion

// Маршрут для вложенного графа, который объединит создание и саму игру
const val GAME_ROUTE = "game"

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.route
    ) {
        // Главное меню
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onNewGame = { navController.navigate(GAME_ROUTE) }, // Переходим на целый граф игры
                onContinueGame = {
                    Toast.makeText(navController.context, "Функция 'Продолжить' в разработке", Toast.LENGTH_SHORT).show()
                },
                onSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        // Выносим всю игровую логику в отдельный вложенный граф
        gameNavGraph(navController)

        // Экран настроек
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onResetGame = {
                    Toast.makeText(navController.context, "Прогресс сброшен!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack(Screen.MainMenu.route, inclusive = false)
                }
            )
        }
    }
}

// Вложенный граф для самой игры
fun NavGraphBuilder.gameNavGraph(navController: androidx.navigation.NavController) {
    navigation(
        startDestination = Screen.CharacterCreation.route, // Стартовый экран этого графа
        route = GAME_ROUTE // Имя (маршрут) всего графа
    ) {
        composable(Screen.CharacterCreation.route) {
            // 1. Получаем точку привязки (ViewModelStoreOwner) из графа навигации
            val viewModelStoreOwner = navController.getBackStackEntry(GAME_ROUTE)
            // 2. Передаем эту точку привязки в viewModel()
            val gameViewModel: MainGameViewModel = viewModel(viewModelStoreOwner = viewModelStoreOwner)

            CharacterCreationScreen(
                onCharacterCreated = { character ->
                    // Просто устанавливаем персонажа
                    gameViewModel.setInitialCharacter(character)
                    // Переходим на следующий экран ВНУТРИ этого же графа
                    navController.navigate(Screen.MainGame.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MainGame.route) {
            // Здесь делаем то же самое, чтобы получить ТОТ ЖЕ самый экземпляр ViewModel
            val viewModelStoreOwner = navController.getBackStackEntry(GAME_ROUTE)
            val gameViewModel: MainGameViewModel = viewModel(viewModelStoreOwner = viewModelStoreOwner)

            MainGameScreen(viewModel = gameViewModel)
        }
    }
}

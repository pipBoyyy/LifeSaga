package com.example.lifesaga.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
import androidx.navigation.NavType // <- ДОБАВЬ ЭТОТ ИМПОРТ
import androidx.navigation.navArgument // <- И ЭТОТ
import com.example.lifesaga.ui.screens.GameOverScreen
import com.example.lifesaga.ui.screens.JobsScreen
import com.example.lifesaga.data.Character
import com.example.lifesaga.ui.screens.SchoolScreen

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
                onNewGame = { navController.navigate(GAME_ROUTE) },
                onContinueGame = {
                    Toast.makeText(navController.context, "Функция 'Продолжить' в разработке", Toast.LENGTH_SHORT).show()
                },
                onSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        // Выносим всю игровую логику в отдельный вложенный граф
        gameNavGraph(navController)

        composable(
            route = "${Screen.GameOver.route}/{age}", // Маршрут с аргументом
            arguments = listOf(navArgument("age") { type = NavType.IntType })
        ) { backStackEntry ->
            // Извлекаем возраст из аргументов
            val age = backStackEntry.arguments?.getInt("age") ?: 0
            GameOverScreen(
                finalAge = age,
                onPlayAgain = {
                    // Возвращаемся в главное меню, очищая все экраны сверху
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.MainMenu.route) { inclusive = true }
                    }
                }
            )
        }
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
fun NavGraphBuilder.gameNavGraph(navController: NavController) {
    navigation(
        startDestination = Screen.CharacterCreation.route,
        route = GAME_ROUTE
    ) {
        composable(Screen.CharacterCreation.route) {
            val viewModelStoreOwner = navController.getBackStackEntry(GAME_ROUTE)
            val gameViewModel: MainGameViewModel = androidx.lifecycle.viewmodel.compose.viewModel(viewModelStoreOwner = viewModelStoreOwner)

            CharacterCreationScreen(
                onCharacterCreated = { characterName: String -> // <- ВОТ ИСПРАВЛЕНИЕ
                    // Теперь компилятор знает, что characterName - это строка
                    val newCharacter = Character(name = characterName)
                    gameViewModel.setInitialCharacter(newCharacter)
                    navController.navigate(Screen.MainGame.route) {
                        popUpTo(Screen.CharacterCreation.route) {
                            inclusive = true
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MainGame.route) {
            // Здесь делаем то же самое, чтобы получить ТОТ ЖЕ самый экземпляр ViewModel
            val viewModelStoreOwner = navController.getBackStackEntry(GAME_ROUTE)
            val gameViewModel: MainGameViewModel = viewModel(viewModelStoreOwner = viewModelStoreOwner)

            MainGameScreen(viewModel = gameViewModel, navController = navController)
        }
        composable(Screen.Jobs.route) {
            val viewModelStoreOwner = navController.getBackStackEntry(GAME_ROUTE)
            val gameViewModel: MainGameViewModel = viewModel(viewModelStoreOwner = viewModelStoreOwner)
            val character = gameViewModel.characterState.collectAsState().value

            if (character != null) {
                JobsScreen(
                    character = character,
                    onJobSelected = { job ->
                        // При выборе работы обновляем персонажа и возвращаемся назад
                        gameViewModel.changeJob(job)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

        }
        composable(Screen.School.route) {
            // Получаем доступ к общему ViewModel, который хранит состояние игры
            val viewModelStoreOwner = navController.getBackStackEntry(GAME_ROUTE)
            val gameViewModel: MainGameViewModel = androidx.lifecycle.viewmodel.compose.viewModel(viewModelStoreOwner = viewModelStoreOwner)

            SchoolScreen(
                onActionSelected = { schoolAction ->
                    // 1. Говорим ViewModel, что нужно обработать действие из школы
                    gameViewModel.handleSchoolAction(schoolAction)

                    // 2. После выбора действия возвращаемся на главный экран
                    navController.popBackStack()
                },
                onBack = {
                    // Кнопка "назад" просто возвращает на предыдущий экран
                    navController.popBackStack()
                }
            )
        }
    }
}

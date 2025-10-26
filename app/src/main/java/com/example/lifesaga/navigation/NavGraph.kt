package com.example.lifesaga.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifesaga.ui.screens.* // Импортируем все экраны

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    // val gameViewModel: GameViewModel = viewModel() // ViewModel пока закомментируем, чтобы убрать ошибки

    // NavHost - это контейнер, который будет показывать нужный экран
    NavHost(
        navController = navController,
        startDestination = Screen.SPLASH.name // Начинаем с заставки
    ) {
        // Заставка (SplashScreen)
        composable(Screen.SPLASH.name) {
            SplashScreen(navController = navController)
        }

        // Главное меню (MainMenuScreen)
        composable(Screen.MAIN_MENU.name) {
            MainMenuScreen(
                onNewGame = { navController.navigate(Screen.CREATE_CHARACTER.name) },
                onContinueGame = {
                    // Здесь будет логика проверки, есть ли сохраненная игра
                    Toast.makeText(navController.context, "Функция 'Продолжить' в разработке", Toast.LENGTH_SHORT).show()
                    // navController.navigate(Screen.GAME.name)
                },
                onSettings = { navController.navigate(Screen.SETTINGS.name) }
            )
        }

        // Экран создания персонажа
        composable(Screen.CREATE_CHARACTER.name) {
            CharacterCreationScreen(
                onCharacterCreated = { character ->
                    // Логика сохранения персонажа и перехода в игру
                    navController.navigate(Screen.GAME.name) {
                        // Очищаем стек до главного меню, чтобы нельзя было вернуться назад на экран создания
                        popUpTo(Screen.MAIN_MENU.name)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Игровой экран (пока будет пустым)
        composable(Screen.GAME.name) {
            // Сюда мы позже добавим GameScreen
            // Пока что можно использовать заглушку
            GameScreenPlaceholder(onMainMenu = { navController.popBackStack(Screen.MAIN_MENU.name, false) })
        }

        // Экран настроек
        composable(Screen.SETTINGS.name) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onResetGame = {
                    // Логика сброса игры
                    navController.popBackStack(Screen.MAIN_MENU.name, false)
                    Toast.makeText(navController.context, "Прогресс сброшен!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

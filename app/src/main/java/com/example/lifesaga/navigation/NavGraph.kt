// В файле navigation/NavGraph.kt
package com.example.lifesaga.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
// import androidx.navigation.ExperimentalNavigationApi // <-- ЭТОТ ИМПОРТ БОЛЬШЕ НЕ НУЖЕН

import com.example.lifesaga.data.AssetRepository
import com.example.lifesaga.ui.screens.*
import com.example.lifesaga.viewmodel.MainGameViewModel

const val GAME_ROUTE = "game"

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    // Создаем ViewModel здесь, на самом высоком уровне, и передаем его вниз
    val gameViewModel: MainGameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.route
    ) {
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onNewGame = {
                    // При старте новой игры, сбрасываем состояние ViewModel
                    gameViewModel.resetGame()
                    navController.navigate(GAME_ROUTE)
                },
                onContinueGame = { Toast.makeText(navController.context, "В разработке", Toast.LENGTH_SHORT).show() },
                onSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        // Передаем ViewModel и NavController в наш вложенный граф
        gameNavGraph(navController, gameViewModel)

        composable(
            route = "${Screen.GameOver.route}/{age}",
            arguments = listOf(navArgument("age") { type = NavType.IntType })
        ) { backStackEntry ->
            val age = backStackEntry.arguments?.getInt("age") ?: 0
            GameOverScreen(
                finalAge = age,
                onPlayAgain = {
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onResetGame = {
                    gameViewModel.resetGame()
                    Toast.makeText(navController.context, "Прогресс сброшен!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack(Screen.MainMenu.route, inclusive = false)
                }
            )
        }
    }
}

// ▼▼▼ КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: ПРИНИМАЕМ ViewModel КАК АРГУМЕНТ ▼▼▼
fun NavGraphBuilder.gameNavGraph(navController: NavController, gameViewModel: MainGameViewModel) {
    navigation(
        startDestination = Screen.CharacterCreation.route,
        route = GAME_ROUTE
    ) {
        // Теперь не нужно получать ViewModel вручную, мы просто используем переданный

        composable(Screen.CharacterCreation.route) {
            CharacterCreationScreen(
                onCharacterCreated = { characterName ->
                    gameViewModel.createNewCharacter(characterName)
                    navController.navigate(Screen.MainGame.route) {
                        popUpTo(Screen.CharacterCreation.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MainGame.route) {
            MainGameScreen(viewModel = gameViewModel, navController = navController)
        }

        composable(Screen.Jobs.route) {
            val character = gameViewModel.characterState.collectAsState().value
            if (character != null) {
                JobsScreen(
                    character = character,
                    onJobSelected = { job ->
                        gameViewModel.changeJob(job)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.School.route) {
            SchoolScreen(
                onActionSelected = { schoolAction ->
                    gameViewModel.handleSchoolAction(schoolAction)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Assets.route) {
            val character = gameViewModel.characterState.collectAsState().value
            if (character != null) {
                AssetsScreen(
                    character = character,
                    availableAssets = AssetRepository.getAvailableAssets(character),                    onBuyAsset = { asset ->
                        gameViewModel.buyAsset(asset)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Relationships.route) {
            // Передаем и ViewModel тоже
            RelationshipsScreen(navController = navController, gameViewModel = gameViewModel)
        }

        composable(Screen.Actions.route) {
            ActionsScreen(navController = navController, gameViewModel = gameViewModel)
        }

    }
}

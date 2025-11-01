// В файле ui/screens/ActionsScreen.kt

package com.example.lifesaga.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifesaga.viewmodel.MainGameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionsScreen(
    navController: NavController,
    gameViewModel: MainGameViewModel
) {
    val characterState by gameViewModel.characterState.collectAsState()
    val character = characterState ?: return

    // ▼▼▼ ПОДПИСЫВАЕМСЯ НА НОВЫЙ STATE ▼▼▼
    val actionResult by gameViewModel.actionResult.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Действия") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Кнопка "Пойти в больницу"
            Button(
                onClick = { gameViewModel.goToHospital() },
                enabled = character.money >= 250 && character.health < 100,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Пойти в больницу (+8 здоровья, -250$)")
            }

            // Кнопка "Заняться спортом"
            Button(
                onClick = { gameViewModel.doSport() },
                enabled = character.energy >= 30 && character.fitness < 10,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Заняться спортом (+1 спорт, -30 энергии)")
            }
        }
    }

    // ▼▼▼ НОВЫЙ ДИАЛОГ ОТОБРАЖЕНИЯ РЕЗУЛЬТАТА ▼▼▼
    actionResult?.let { result ->
        AlertDialog(
            onDismissRequest = { gameViewModel.clearActionResult() },
            title = { Text("Результат действия") },
            text = { Text(result.message) }, // Показываем сообщение из ViewModel
            confirmButton = {
                Button(onClick = { gameViewModel.clearActionResult() }) {
                    Text("Отлично")
                }
            }
        )
    }
}

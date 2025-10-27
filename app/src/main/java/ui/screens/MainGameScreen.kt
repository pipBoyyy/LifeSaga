package com.example.lifesaga.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifesaga.navigation.Screen
import com.example.lifesaga.viewmodel.MainGameViewModel

@Composable
fun MainGameScreen(
    viewModel: MainGameViewModel,
    navController: NavController
) {
    val character by viewModel.characterState.collectAsState()
    val event by viewModel.currentEvent.collectAsState()
    val finalAge by viewModel.gameOverState.collectAsState()

    LaunchedEffect(finalAge) {
        finalAge?.let { age ->
            navController.navigate("${Screen.GameOver.route}/$age")
            viewModel.onGameOverScreenNavigated()
        }
    }

    if (character == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // --- НАЧАЛО БЛОКА ELSE ---

        // 1. ИСПОЛЬЗУЕМ BOX КАК ГЛАВНЫЙ КОНТЕЙНЕР
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 2. КОЛОНКА С ПАРАМЕТРАМИ, ПРИЖАТАЯ К ВЕРХУ
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter), // Прижимаем к верху Box
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = character!!.name,
                    style = MaterialTheme.typography.displaySmall
                )
                character!!.currentJob?.let {
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                CharacterStatsRow(label = "Возраст", value = character!!.age.toString())
                CharacterStatsRow(label = "Здоровье", value = character!!.health.toString())
                CharacterStatsRow(label = "Деньги", value = "${character!!.money} $")
                CharacterStatsRow(label = "Счастье", value = character!!.happiness.toString())
                CharacterStatsRow(label = "Ум", value = character!!.smarts.toString())
                if (character!!.age < 18) {
                    CharacterStatsRow(label = "Успеваемость", value = character!!.schoolPerformance.toString())
                }
            }

            // 3. РЯД С КНОПКАМИ, ПРИЖАТЫЙ К НИЗУ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter), // Прижимаем к низу Box
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (character!!.age < 18) {
                    // ЕСЛИ ШКОЛЬНИК: показываем кнопку "Школа"
                    Button(
                        onClick = { navController.navigate(Screen.School.route) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Школа")
                    }
                } else if (character!!.currentJob == null) {
                    // ЕСЛИ ВЗРОСЛЫЙ И БЕЗРАБОТНЫЙ: кнопка "Найти работу"
                    Button(
                        onClick = { navController.navigate(Screen.Jobs.route) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Найти работу")
                    }
                } else {
                    // ЕСЛИ ВЗРОСЛЫЙ И РАБОТАЕТ: кнопка "Уволиться"
                    Button(
                        onClick = { viewModel.quitJob() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Уволиться")
                    }
                }

                // Кнопка "Следующий год" всегда на месте
                Button(
                    onClick = { viewModel.nextYear() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("След. год") // Убрал лишний параметр fontSize, лучше управлять стилями централизованно
                }
            }
        }

        // Диалог события остается на своем месте
        event?.let { currentEvent ->
            GameEventDialog(
                event = currentEvent,
                onChoiceSelected = { choice ->
                    viewModel.handleEventChoice(choice)
                }
            )
        }

    } // --- КОНЕЦ БЛОКА ELSE ---
}

@Composable
fun CharacterStatsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 20.sp)
        Text(text = value, fontSize = 20.sp, style = MaterialTheme.typography.bodyLarge)
    }
}

// В файле ui/screens/MainGameScreen.kt

package com.example.lifesaga.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    // 1. ПОДПИСЫВАЕМСЯ НА НОВЫЙ СПИСОК СОБЫТИЙ
    val yearLog by viewModel.yearEventsLog.collectAsState()

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Колонка с основной информацией (статистика + ЛОГ СОБЫТИЙ)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                // --- Блок со статистикой персонажа (остается без изменений) ---
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(text = character!!.name, style = MaterialTheme.typography.displaySmall)
                    character!!.currentJob?.let {
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
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

                // --- 2. НОВЫЙ БЛОК: ЛОГ СОБЫТИЙ ЗА ГОД ---
                Spacer(modifier = Modifier.height(24.dp))
                Text("События за год:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                // Используем LazyColumn для прокручиваемого списка
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Занимает всё доступное место между статистикой и кнопками
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // items - функция, которая строит элементы списка
                    items(yearLog) { logEntry ->
                        Text(
                            text = "• $logEntry", // Добавляем маркер для красоты
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }


            // --- Ряд с кнопками внизу (остается почти без изменений) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Логика кнопок остается прежней
                if (character!!.age < 18) {
                    Button(
                        onClick = { navController.navigate(Screen.School.route) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Школа")
                    }
                } else if (character!!.currentJob == null) {
                    Button(
                        onClick = { navController.navigate(Screen.Jobs.route) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Найти работу")
                    }
                } else {
                    Button(onClick = { viewModel.quitJob() }, modifier = Modifier.weight(1f)) {
                        Text("Уволиться")
                    }
                }

                Button(onClick = { viewModel.nextYear() }, modifier = Modifier.weight(1f)) {
                    Text("След. год")
                }
            }
        }

        // Диалог для событий С ВЫБОРОМ остается на месте
        event?.let { currentEvent ->
            GameEventDialog(
                event = currentEvent,
                onChoiceSelected = { choice ->
                    viewModel.handleEventChoice(choice)
                }
            )
        }
    }
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

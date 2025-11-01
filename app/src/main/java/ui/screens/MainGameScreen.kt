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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifesaga.R
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
    val yearLog by viewModel.yearEventsLog.collectAsState()

    LaunchedEffect(finalAge) {
        finalAge?.let { age ->
            navController.navigate("${Screen.GameOver.route}/$age")
            viewModel.onGameOverScreenNavigated()
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                // Элемент 1: Школа/Работа (без подписи)
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        if (character!!.age < 18) {
                            navController.navigate(Screen.School.route)
                        } else {
                            navController.navigate(Screen.Jobs.route)
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_job),
                            contentDescription = "Школа или Работа",
                            modifier = Modifier.size(70.dp), // Сделал чуть-чуть побольше
                            tint = Color.Unspecified
                        )
                    }
                    // label = { Text("Занятие") } // <-- Убрали подпись
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Relationships.route) },                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_relationships), // Убедись, что у тебя есть эта иконка
                            contentDescription = "Отношения",
                            modifier = Modifier.size(70.dp),
                            tint = Color.Unspecified
                        )
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Actions.route) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_actions), // Убедись, что иконка с таким именем есть
                            contentDescription = "Действия",
                            modifier = Modifier.size(70.dp),
                            tint = Color.Unspecified
                        )
                    }
                )

                // Элемент 2: Имущество (без подписи)
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Assets.route) },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_assets),
                            contentDescription = "Имущество",
                            modifier = Modifier.size(70.dp), // Сделал чуть-чуть побольше
                            tint = Color.Unspecified
                        )
                    }
                    // label = { Text("Имущество") } // <-- Убрали подпись
                )

                // Элемент 3: Следующий год (без подписи)
                NavigationBarItem(
                    selected = false,
                    onClick = { viewModel.nextYear() },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_next_year),
                            contentDescription = "Следующий год",
                            modifier = Modifier.size(70.dp), // Сделал чуть-чуть побольше
                            tint = Color.Unspecified
                        )
                    }
                    // label = { Text("Год +") } // <-- Убрали подпись
                )
            }
        }
    ) { innerPadding ->
        if (character == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // --- Блок со статистикой персонажа ---
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(text = character!!.name, style = MaterialTheme.typography.displaySmall)
                        character!!.currentJob?.let {
                            Text(
                                text = it.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    CharacterStatsRow(label = "Возраст", value = character!!.age.toString())
                    CharacterStatsRow(label = "Здоровье", value = character!!.health.toString())
                    CharacterStatsRow(label = "Счастье", value = character!!.happiness.toString())
                    CharacterStatsRow(label = "Деньги", value = "${character!!.money}$")
                    CharacterStatsRow(label = "Ум", value = character!!.smarts.toString())
                    CharacterStatsRow(label = "Энергия", value = character!!.energy.toString())
                    CharacterStatsRow(label = "Спорт", value = character!!.fitness.toString())
                    // ▼▼▼ ВОТ ОНА, ВЕРНУЛАСЬ! ▼▼▼
                    Spacer(modifier = Modifier.height(24.dp))

                    // --- ЛОГ СОБЫТИЙ ЗА ГОД ---
                    Text(
                        "События за год:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        items(yearLog) { logEntry ->
                            Text(
                                text = "• $logEntry",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Диалог для событий
            event?.let { currentEvent ->
                GameEventDialog(
                    event = currentEvent,
                    onChoiceSelected = { choice -> viewModel.handleEventChoice(choice) }
                )
            }
        }
    }
}

// Функция CharacterStatsRow остается без изменений.
// Убедись, что она у тебя есть в файле.
@Composable
fun CharacterStatsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

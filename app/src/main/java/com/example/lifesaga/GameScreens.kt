package com.example.lifesaga // Убедись, что это твой пакет

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
// import androidx.preference.forEach  // <--- ЭТУ СТРОКУ Я УДАЛИЛ

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel(),
    onMainMenu: () -> Unit
) {
    // Получаем состояние из ViewModel
    val character by gameViewModel.character.collectAsState()
    val currentYear by gameViewModel.currentYear.collectAsState()
    val eventOutcomeMessage by gameViewModel.eventOutcomeMessage.collectAsState()
    val showEventDialog by gameViewModel.showEventDialog.collectAsState()
    val currentEvent by gameViewModel.currentEvent.collectAsState()
    val isGameOver by gameViewModel.isGameOver.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LifeSaga") },
                navigationIcon = {
                    IconButton(onClick = onMainMenu) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Главное меню")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // Отображение года, если персонаж существует
            character?.let {
                Text("Год: $currentYear", style = MaterialTheme.typography.headlineMedium)
            }

            // Основная информация о персонаже
            character?.let { char ->
                // Карточка персонажа
                CharacterCard(character = char)
                Spacer(Modifier.height(16.dp))

                // Отображение сообщения о результате события
                eventOutcomeMessage?.let { message ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Кнопка "Прожить год"
                if (!isGameOver && !showEventDialog) {
                    Button(
                        onClick = { gameViewModel.passYear() },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(16.dp)
                            .height(56.dp)
                    ) {
                        Text("Прожить еще год", fontSize = 20.sp)
                    }
                }

                // Кнопка "Начать заново"
                if (isGameOver) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            gameViewModel.resetGame()
                            onMainMenu()
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Начать заново", fontSize = 20.sp)
                    }
                }

            } ?: run {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Загрузка персонажа...", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

    // --- ДИАЛОГ СОБЫТИЯ ---
    if (showEventDialog && currentEvent != null) {
        AlertDialog(
            onDismissRequest = { /* Оставляем пустым, чтобы пользователь делал выбор */ },
            title = {
                currentEvent?.eventText?.let {
                    Text(it, style = MaterialTheme.typography.headlineSmall)
                }
            },
            text = {
                eventOutcomeMessage?.let {
                    Text(it, style = MaterialTheme.typography.bodyLarge)
                }
            },
            confirmButton = {
                if (eventOutcomeMessage != null) {
                    // Если исход уже применен, показываем кнопку "OK"
                    Button(
                        onClick = {
                            gameViewModel.hideEventDialog()
                            gameViewModel.clearEventOutcomeMessage()
                        }
                    ) {
                        Text("OK")
                    }
                } else {
                    // Если исхода еще нет, показываем кнопки выбора
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currentEvent?.choices?.forEach { choice ->
                            Button(
                                onClick = { gameViewModel.applyOutcome(choice.outcome) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Здесь все правильно, используется поле `description`
                                Text(choice.description)
                            }
                        }
                    }
                }
            }
        )
    }
}

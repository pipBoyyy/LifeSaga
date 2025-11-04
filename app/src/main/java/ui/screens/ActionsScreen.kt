// В файле: app/src/main/java/com/example/lifesaga/ui/screens/ActionsScreen.kt
// ПОЛНОСТЬЮ ЗАМЕНИ СТАРЫЙ КОД НА ЭТОТ

package com.example.lifesaga.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifesaga.data.PartTimeJobRepository
import com.example.lifesaga.viewmodel.MainGameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionsScreen(
    navController: NavController,
    gameViewModel: MainGameViewModel
) {
    val characterState by gameViewModel.characterState.collectAsState()
    val character = characterState ?: return

    val actionResult by gameViewModel.actionResult.collectAsState()

    // Диалог для отображения результата любого действия
    actionResult?.let { result ->
        AlertDialog(
            onDismissRequest = { gameViewModel.clearActionResult() },
            title = { Text("Результат действия") },
            text = { Text(result.message) },
            confirmButton = {
                Button(onClick = { gameViewModel.clearActionResult() }) {
                    Text("Отлично")
                }
            }
        )
    }

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
        // Используем LazyColumn для прокручиваемого списка
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- КАТЕГОРИЯ: ЗДОРОВЬЕ И СПОРТ ---
            item {
                Text("Здоровье и спорт", style = MaterialTheme.typography.titleLarge)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
            item {
                Button(
                    onClick = { gameViewModel.goToHospital() },
                    enabled = character.money >= 250 && character.health < 100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Пойти в больницу (+8 здоровья, -250$)")
                }
            }
            item {
                Button(
                    onClick = { gameViewModel.doSport() },
                    enabled = character.energy >= 30 && character.fitness < 10,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Заняться спортом (+1 спорт, -30 энергии)")
                }
            }

            // --- КАТЕГОРИЯ: ПОДРАБОТКА (НОВАЯ ЛОГИКА) ---
            if (character.age >= 14) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Подработка", style = MaterialTheme.typography.titleLarge)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }

                // Динамически создаем карточки для каждой подработки
                items(PartTimeJobRepository.getAvailablePartTimeJobs()) { job ->
                    val canAffordEnergy = character.energy >= job.energyCost
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { if (canAffordEnergy) gameViewModel.doPartTimeJob(job) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (canAffordEnergy) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(job.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(job.description, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Энергия: -${job.energyCost}",
                                    color = if (canAffordEnergy) LocalContentColor.current else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Доход: +${job.moneyGain}$",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

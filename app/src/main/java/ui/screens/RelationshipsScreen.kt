package com.example.lifesaga.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifesaga.data.PersonRepository
import com.example.lifesaga.viewmodel.MainGameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelationshipsScreen(
    navController: NavController,
    gameViewModel: MainGameViewModel
) {
    val character by gameViewModel.characterState.collectAsState()
    val personToInteract by gameViewModel.personToInteract.collectAsState()
    // ▼▼▼ НОВЫЙ STATE ДЛЯ РЕЗУЛЬТАТА ▼▼▼
    val interactionResult by gameViewModel.interactionResult.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отношения") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            character?.let { char ->
                items(
                    items = char.relationships,
                    key = { it.personId }
                ) { relationship ->
                    val person = PersonRepository.getPersonById(relationship.personId)
                    person?.let { currentPerson ->
                        Box(modifier = Modifier.clickable {
                            gameViewModel.selectPersonToInteract(currentPerson)
                        }) {
                            RelationshipItem(
                                person = currentPerson,
                                relationship = relationship
                            )
                        }
                    }
                }
            }
        }
    }

    // --- ДИАЛОГ ВЫБОРА ДЕЙСТВИЯ ---
    personToInteract?.let { person ->
        AlertDialog(
            onDismissRequest = { gameViewModel.clearPersonToInteract() },
            title = { Text("Взаимодействие с ${person.name}") },
            text = {
                // ▼▼▼ ОБНОВЛЕННЫЙ БЛОК С КНОПКАМИ ДЕЙСТВИЙ ▼▼▼
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { gameViewModel.interactWithPerson("talk") }) {
                        Text("Поговорить (15 энергии)")
                    }
                    Button(onClick = { gameViewModel.interactWithPerson("compliment") }) {
                        Text("Комплимент (20 энергии)")
                    }
                    Button(onClick = { gameViewModel.interactWithPerson("argue") }) {
                        Text("Поспорить (35 энергии)")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { gameViewModel.clearPersonToInteract() }) {
                    Text("Закрыть")
                }
            }
        )
    }

    // --- НОВЫЙ ДИАЛОГ ОТОБРАЖЕНИЯ РЕЗУЛЬТАТА ---
    interactionResult?.let { result ->
        AlertDialog(
            onDismissRequest = { gameViewModel.clearInteractionResult() },
            title = { Text("Результат взаимодействия") },
            text = { Text(result.message) }, // Показываем сообщение из ViewModel
            confirmButton = {
                Button(onClick = { gameViewModel.clearInteractionResult() }) {
                    Text("Отлично")
                }
            }
        )
    }
}

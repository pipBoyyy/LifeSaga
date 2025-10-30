// Полностью замени содержимое файла ui/screens/RelationshipsScreen.kt
package com.example.lifesaga.ui.screens

import androidx.compose.foundation.clickable // <-- Убедись, что импорт есть
import androidx.compose.foundation.layout.Box
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
                // ▼▼▼ ВАЖНЕЙШЕЕ ИЗМЕНЕНИЕ ЗДЕСЬ ▼▼▼
                items(
                    items = char.relationships,
                    key = { it.personId } // Уникальный ключ для элемента списка
                ) { relationship ->
                    val person = PersonRepository.getPersonById(relationship.personId)
                    person?.let { currentPerson ->
                        // Мы создаем Box просто чтобы повесить на него clickable
                        Box(modifier = Modifier.clickable {
                            gameViewModel.selectPersonToInteract(currentPerson)
                        }) {
                            RelationshipItem(
                                person = currentPerson,
                                relationship = relationship
                                // onClick больше не передается
                            )
                        }
                    }
                }
            }
        }
    }

    personToInteract?.let { person ->
        AlertDialog(
            onDismissRequest = { gameViewModel.clearPersonToInteract() },
            title = { Text("Взаимодействие с ${person.name}") },
            text = { Text("Здесь будут кнопки действий.") },
            confirmButton = {
                Button(onClick = { gameViewModel.clearPersonToInteract() }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

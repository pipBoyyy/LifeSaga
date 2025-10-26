package com.example.lifesaga

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationScreen(
    vm: GameViewModel = viewModel(),
    onBack: () -> Unit
) {
    // Состояние персонажа из ViewModel
    val character by vm.character.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Учёба") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Отображаем текущий уровень образования
            Text(
                text = "Уровень: ${character?.educationLevel ?: EducationLevel.NONE}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))

            // Кнопка «Учиться»
            Button(onClick = { vm.study() }) {
                Text("Учиться (−20 энергии, +5 интеллекта)")
            }
            Spacer(Modifier.height(8.dp))

            // Кнопка «Прогулять урок»
            Button(onClick = { /* vm.skipClass() */ }) {
                Text("Прогулять урок (+10 счастья)")
            }
            Spacer(Modifier.height(8.dp))

            // Кнопка «Списать»
            Button(onClick = { /* vm.cheat() */ }) {
                Text("Списать (шанс успеха зависит от интеллекта)")
            }
            Spacer(Modifier.height(24.dp))

            // Кнопка «Вернуться»
            Button(onClick = onBack) {
                Text("Вернуться")
            }
        }
    }
}

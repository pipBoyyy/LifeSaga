package com.example.lifesaga.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Создаем перечисление (enum) для четкого определения действий, которые можно совершать в школе
enum class SchoolAction {
    STUDY_HARD, // Учиться усердно
    SLACK_OFF   // Бездельничать/прогуливать
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolScreen(
    onActionSelected: (SchoolAction) -> Unit, // Функция, которая вернет нам выбранное действие
    onBack: () -> Unit // Функция для кнопки "Назад"
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Школа") },
                navigationIcon = {
                    // Стандартная кнопка "назад" в левом верхнем углу
                    IconButton(onClick = onBack) {
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Что будете делать?", fontSize = 22.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка для усердной учебы
            Button(
                onClick = { onActionSelected(SchoolAction.STUDY_HARD) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Учиться усердно")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка для прогулов
            Button(
                onClick = { onActionSelected(SchoolAction.SLACK_OFF) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Прогуливать")
            }
        }
    }
}

package com.example.lifesaga.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lifesaga.data.Character // <- Убедись, что этот импорт добавился

@Composable
fun CharacterCreationScreen(
    onCharacterCreated: (Character) -> Unit,
    onBack: () -> Unit // Пока не используется, но оставим на будущее
) {
    // 1. Создаем состояние для хранения введенного имени
    var name by remember { mutableStateOf("") }
    // 2. Состояние для отображения ошибки, если имя пустое
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Создание персонажа", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(32.dp))

        // 3. Поле для ввода имени
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                if (isError) isError = false // Сбрасываем ошибку при начале ввода
            },
            label = { Text("Введите имя") },
            singleLine = true,
            isError = isError // Поле станет красным, если есть ошибка
        )
        if (isError) {
            Text(
                text = "Имя не может быть пустым!",
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Кнопка для создания персонажа
        Button(onClick = {
            if (name.isNotBlank()) {
                // Если имя не пустое, создаем персонажа и передаем его дальше
                val newCharacter = Character(name = name)
                onCharacterCreated(newCharacter)
            } else {
                // Если имя пустое, показываем ошибку
                isError = true
            }
        }) {
            Text("Начать новую жизнь")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CharacterCreationScreenPreview() {
    CharacterCreationScreen(onCharacterCreated = {}, onBack = {})
}


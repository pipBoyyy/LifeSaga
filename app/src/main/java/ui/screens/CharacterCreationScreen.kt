package com.example.lifesaga.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.text.isNotBlank

// Не импортируем Character, он здесь больше не нужен
// import com.example.lifesaga.data.Character

@androidx.compose.runtime.Composable
fun CharacterCreationScreen(
    // 1. ИЗМЕНЕНИЕ: Теперь ожидаем на вход просто строку (имя)
    onCharacterCreated: (String) -> Unit,
    onBack: () -> Unit
) {
    var name by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var isError by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text("Создание персонажа", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                if (isError) isError = false
            },
            label = { Text("Введите имя") },
            singleLine = true,
            isError = isError
        )
        if (isError) {
            Text(
                text = "Имя не может быть пустым!",
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            if (name.isNotBlank()) {
                // 2. ИЗМЕНЕНИЕ: Передаем дальше только строку с именем
                onCharacterCreated(name)
            } else {
                isError = true
            }
        }) {
            Text("Начать новую жизнь")
        }
    }
}

@Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun CharacterCreationScreenPreview() {
    // В превью тоже передаем пустую лямбду, ожидающую String
    CharacterCreationScreen(onCharacterCreated = {}, onBack = {})
}

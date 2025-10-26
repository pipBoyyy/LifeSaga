package com.example.lifesaga.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.lifesaga.data.EventChoice
import com.example.lifesaga.data.GameEvent

@Composable
fun GameEventDialog(
    event: GameEvent,
    onChoiceSelected: (EventChoice) -> Unit
) {
    // Dialog - это специальный Composable, который рисуется поверх всего экрана
    Dialog(onDismissRequest = { /* Не разрешаем закрывать диалог свайпом или кнопкой "назад" */ }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Создаем кнопки для каждого варианта выбора
                event.choices.forEach { choice ->
                    Button(
                        onClick = { onChoiceSelected(choice) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = choice.description)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

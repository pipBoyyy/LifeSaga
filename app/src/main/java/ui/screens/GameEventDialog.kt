// В файле ui/screens/GameEventDialog.kt

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.lifesaga.data.EventChoice
import com.example.lifesaga.data.GameEvent

@Composable
fun GameEventDialog(
    event: GameEvent,
    onChoiceSelected: (EventChoice) -> Unit
) {
    Dialog(onDismissRequest = { /* Не разрешаем закрывать диалог */ }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // ИЗМЕНЕНИЕ №1: Убрали заголовок, используем только description.
                // Сделаем текст чуть крупнее, чтобы он был главным.
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyLarge, // Чуть увеличили шрифт
                )
                Spacer(modifier = Modifier.height(24.dp))

                // ИЗМЕНЕНИЕ №2: Создаем кнопки для каждого варианта выбора
                event.choices.forEach { choice ->
                    Button(
                        onClick = { onChoiceSelected(choice) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Используем choice.text вместо choice.description
                        Text(text = choice.text)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

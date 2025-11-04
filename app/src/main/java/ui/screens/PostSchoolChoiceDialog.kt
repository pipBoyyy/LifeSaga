// В файле: app/src/main/java/com/example/lifesaga/ui/composables/PostSchoolChoiceDialog.kt

package com.example.lifesaga.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// Этот enum поможет нам передавать выбор пользователя обратно во ViewModel
enum class PostSchoolChoice {
    UNIVERSITY,
    WORK,
    ARMY
}

@Composable
fun PostSchoolChoiceDialog(
    onDismissRequest: () -> Unit, // Важно: что делать, если пользователь просто закрыл окно
    onChoiceMade: (PostSchoolChoice) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest, // Не даем закрыть окно просто так
        title = { Text(text = "Вы окончили школу!") },
        text = {
            Text(
                text = "Поздравляем с важным этапом! Перед вами открыты все дороги. Какой путь вы выберете?",
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onChoiceMade(PostSchoolChoice.UNIVERSITY) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Поступить в университет")
                }
                Button(
                    onClick = { onChoiceMade(PostSchoolChoice.WORK) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Найти работу")
                }
                Button(
                    onClick = { onChoiceMade(PostSchoolChoice.ARMY) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false // Пока что выключим армию
                ) {
                    Text("Пойти в армию (в разработке)")
                }
            }
        }
    )
}

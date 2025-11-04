// В файле: app/src/main/java/com/example/lifesaga/ui/screens/EnrollmentScreen.kt

package com.example.lifesaga.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lifesaga.data.University

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentScreen(
    availableUniversities: List<University>,
    onEnroll: (University) -> Unit,
    onBack: () -> Unit,
    currentMoney: Int
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Поступление в университет") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Выберите учебное заведение. Поступление возможно, если у вас достаточно ума и денег на первый год обучения.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            items(availableUniversities) { university ->
                val canAfford = currentMoney >= university.tuitionFee
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = university.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Стоимость в год: ${university.tuitionFee}$")
                        Text("Срок обучения: ${university.yearsToComplete} года/лет")
                        Text("Требуемый интеллект: ${university.requiredSmarts}")
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { onEnroll(university) },
                            enabled = canAfford, // Кнопка активна, только если хватает денег
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (canAfford) "Поступить" else "Недостаточно денег")
                        }
                    }
                }
            }

            if (availableUniversities.isEmpty()) {
                item {
                    Text(
                        "К сожалению, с вашим текущим уровнем интеллекта вы не можете поступить ни в один университет. Попробуйте найти работу.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            }
        }
    }
}

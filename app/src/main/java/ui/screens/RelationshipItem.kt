// В файле ui/screens/RelationshipItem.kt
package com.example.lifesaga.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lifesaga.data.Person
import com.example.lifesaga.data.Relationship
import java.util.Locale

@Composable
fun RelationshipItem( // <-- УБРАЛИ ПАРАМЕТР onClick
    person: Person,
    relationship: Relationship
) {
    Card(
        modifier = Modifier // <-- УБРАЛИ .clickable
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // ... остальной код карточки без изменений ...
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${person.name} (${person.relationshipType.name.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() }})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Возраст: ${person.age}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Отношения: ${relationship.relationshipMeter}", style = MaterialTheme.typography.bodySmall)
            val progress = if (relationship.relationshipMeter > 0) relationship.relationshipMeter / 100f else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color.Green,
                trackColor = Color.Green.copy(alpha = 0.2f)
            )
        }
    }
}

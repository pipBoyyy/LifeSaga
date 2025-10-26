package com.example.lifesaga.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lifesaga.data.Character
import com.example.lifesaga.data.Job
import com.example.lifesaga.data.JobRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(
    character: Character,
    onJobSelected: (Job) -> Unit,
    onBack: () -> Unit
) {
    val availableJobs = JobRepository.getAvailableJobs(character)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Доступные вакансии") },
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
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            if (availableJobs.isEmpty()) {
                item {
                    Text(
                        text = "Для вас пока нет подходящих вакансий.",
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            } else {
                items(availableJobs) { job ->
                    JobItem(job = job, onJobSelected = onJobSelected)
                }
            }
        }
    }
}

@Composable
fun JobItem(job: Job, onJobSelected: (Job) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onJobSelected(job) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = job.title, style = MaterialTheme.typography.titleLarge)
                Text(text = "Зарплата: ${job.salary}$ в год")
            }
        }
    }
}

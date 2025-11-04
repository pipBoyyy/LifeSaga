// В файле: StudyScreen.kt
// ПОЛНОСТЬЮ ЗАМЕНИ КОД

package com.example.lifesaga.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifesaga.data.Character // Убедись, что импорт правильный
import com.example.lifesaga.data.UniversityRepository
import com.example.lifesaga.viewmodel.*
import com.example.lifesaga.data.SchoolAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    navController: NavController,
    viewModel: MainGameViewModel
) {
    val character by viewModel.characterState.collectAsState()

    // Определяем, кем является персонаж
    val isStudent = character?.universityId != null
    // Школьник - если возраст от 6 до 17 и он не является студентом
    val isSchoolboy = character?.age in 6..17 && !isStudent

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isStudent) "Университет" else "Школа")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Используем navController
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // В зависимости от статуса, показываем нужный контент
            when {
                isStudent && character != null -> UniversityContent(navController, viewModel, character!!)
                isSchoolboy && character != null -> SchoolContent(navController, viewModel, character!!)
                else -> Text("Нет доступных действий для учебы. Возможно, вы уже окончили школу.")
            }
        }
    }
}

// Контент для школы
@Composable
private fun SchoolContent(navController: NavController, viewModel: MainGameViewModel, character: Character) {
    Text("Школьная успеваемость: ${character.schoolPerformance}%", style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(32.dp))
    Text("Что будете делать в этом году?", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)

    Button(
        onClick = {
            viewModel.handleSchoolAction(SchoolAction.STUDY_HARD)
            navController.popBackStack() // Возвращаемся на главный экран после действия
        },
        modifier = Modifier.fillMaxWidth()
    ) { Text("Учиться усердно") }

    Button(
        onClick = {
            viewModel.handleSchoolAction(SchoolAction.SLACK_OFF)
            navController.popBackStack() // Возвращаемся на главный экран после действия
        },
        modifier = Modifier.fillMaxWidth()
    ) { Text("Ничего не делать") }
}

// Контент для университета
@Composable
private fun UniversityContent(navController: NavController, viewModel: MainGameViewModel, character: Character) {
    val university = UniversityRepository.getUniversityById(character.universityId!!)

    Text("Курс: ${character.yearsInUniversity} из ${university?.yearsToComplete}", style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(16.dp))

    // Кнопка 1: Вечеринка
    Button(
        onClick = {
            viewModel.handleUniversityAction(UniversityAction.GO_TO_PARTY)
            navController.popBackStack()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Сходить на вечеринку (-35⚡, +15😊, +2👥)")
    }
    // Кнопка 2: Учиться
    Button(
        onClick = {
            viewModel.handleUniversityAction(UniversityAction.STUDY_HARD)
            navController.popBackStack()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Учиться усердно (-25⚡, -5😊, +5💡)")
    }
    // Кнопка 3: Знакомства
    Button(
        onClick = {
            viewModel.handleUniversityAction(UniversityAction.MEET_PEOPLE)
            navController.popBackStack()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Познакомиться с людьми (-15⚡, +5😊, +1👤)")
    }
    // Кнопка 4: Прогулять
    Button(
        onClick = {
            viewModel.handleUniversityAction(UniversityAction.SKIP_CLASSES)
            navController.popBackStack()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Прогулять пары (+15⚡, +5😊, -5💡)")
    }
}

package com.example.lifesaga.viewmodel

import androidx.compose.animation.core.copy
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.coerceAtMost
import androidx.lifecycle.ViewModel
import com.example.lifesaga.data.Character
import com.example.lifesaga.data.EventChoice
import com.example.lifesaga.data.EventRepository
import com.example.lifesaga.data.GameEvent
import com.example.lifesaga.data.Job
import com.example.lifesaga.ui.screens.SchoolAction // <- Студия может попросить импортировать это
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainGameViewModel : ViewModel() {

    private val _characterState = MutableStateFlow<Character?>(null)
    val characterState = _characterState.asStateFlow()

    private val _currentEvent = MutableStateFlow<GameEvent?>(null)
    val currentEvent = _currentEvent.asStateFlow()

    private val _gameOverState = MutableStateFlow<Int?>(null)
    val gameOverState = _gameOverState.asStateFlow()

    fun setInitialCharacter(character: Character) {
        _characterState.update { character }
        _gameOverState.update { null }
    }

    // --- НОВАЯ ФУНКЦИЯ ДЛЯ ОБРАБОТКИ ДЕЙСТВИЙ В ШКОЛЕ ---
    fun performSchoolAction(action: SchoolAction) {
        _characterState.update { character ->
            when (action) {
                // Усердная учеба: +успеваемость, +ум, -счастье
                SchoolAction.STUDY_HARD -> character?.copy(
                    schoolPerformance = (character.schoolPerformance + 8).coerceAtMost(100),
                    smarts = (character.smarts + 2).coerceAtMost(100),
                    happiness = (character.happiness - 5).coerceAtLeast(0)
                )
                // Прогулы: -успеваемость, +счастье
                SchoolAction.SLACK_OFF -> character?.copy(
                    schoolPerformance = (character.schoolPerformance - 10).coerceAtLeast(0),
                    happiness = (character.happiness + 7).coerceAtMost(100)
                )
            }
        }
    }
    // ----------------------------------------------------

    fun nextYear() {
        val character = _characterState.value ?: return
        val event = EventRepository.getRandomEvent(character)

        if (event == null) {
            handleEventChoice(EventChoice("Просто прошел год...", { it }))
        } else {
            _currentEvent.update { event }
        }
    }

    fun handleEventChoice(choice: EventChoice) {
        val currentCharacter = _characterState.value ?: return
        var updatedCharacter = choice.action(currentCharacter)

        // --- ОБНОВЛЕНА ЛОГИКА ДОХОДА ---
        // Если есть работа -> ЗП. Если >18 лет и нет работы -> 500. Если ребенок -> 0.
        val income = if (updatedCharacter.currentJob != null) {
            updatedCharacter.currentJob!!.salary
        } else if (updatedCharacter.age >= 18) {
            500
        } else {
            0
        }

        updatedCharacter = updatedCharacter.copy(
            age = updatedCharacter.age + 1,
            money = updatedCharacter.money + income
        )

        _characterState.update { updatedCharacter }
        _currentEvent.update { null }

        checkGameOver(updatedCharacter)
    }

    private fun checkGameOver(character: Character) {
        if (character.health <= 0) {
            _gameOverState.update { character.age }
            return
        }
        if (character.age > 80) {
            val deathChance = (character.age - 80) * 0.1
            if (java.lang.Math.random() < deathChance) {
                _gameOverState.update { character.age }
            }
        }
    }

    fun onGameOverScreenNavigated() {
        _gameOverState.update { null }
    }

    fun changeJob(newJob: Job) {
        _characterState.update { character ->
            character?.copy(currentJob = newJob)
        }
    }

    fun quitJob() {
        _characterState.update { character ->
            character?.copy(currentJob = null)
        }
    }
}

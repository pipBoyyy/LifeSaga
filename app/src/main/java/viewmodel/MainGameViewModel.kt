package com.example.lifesaga.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lifesaga.data.Character
import com.example.lifesaga.data.EventChoice
import com.example.lifesaga.data.EventRepository
import com.example.lifesaga.data.GameEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainGameViewModel : ViewModel() {

    private val _characterState = MutableStateFlow<Character?>(null)
    val characterState = _characterState.asStateFlow()

    private val _currentEvent = MutableStateFlow<GameEvent?>(null)
    val currentEvent = _currentEvent.asStateFlow()

    // --- НОВОЕ СОСТОЯНИЕ ДЛЯ КОНЦА ИГРЫ ---
    // Если не null, значит игра окончена. Хранит итоговый возраст.
    private val _gameOverState = MutableStateFlow<Int?>(null)
    val gameOverState = _gameOverState.asStateFlow()
    // ------------------------------------

    fun setInitialCharacter(character: Character) {
        _characterState.update { character }
        _gameOverState.update { null } // Сбрасываем состояние конца игры при старте новой
    }

    fun nextYear() {
        val character = _characterState.value ?: return
        val event = EventRepository.getRandomEvent(character)

        // Если подходящих событий нет, просто проживаем год.
        if (event == null) {
            handleEventChoice(EventChoice("Просто прошел год...", { it }))
        } else {
            _currentEvent.update { event }
        }
    }

    fun handleEventChoice(choice: EventChoice) {
        val currentCharacter = _characterState.value ?: return

        var updatedCharacter = choice.action(currentCharacter)

        // Если есть работа, используем ее зарплату, иначе — базовый доход
        val income = updatedCharacter.currentJob?.salary ?: 500

        updatedCharacter = updatedCharacter.copy(
            age = updatedCharacter.age + 1,
            money = updatedCharacter.money + income // Используем доход
        )

        _characterState.update { updatedCharacter }
        _currentEvent.update { null }

        checkGameOver(updatedCharacter)
    }

    private fun checkGameOver(character: Character) {
        // Условие 1: Здоровье упало до 0 или ниже
        if (character.health <= 0) {
            _gameOverState.update { character.age }
            return // Выходим, чтобы не проверять другие условия
        }

        // Условие 2: Смерть от старости (шанс увеличивается после 80 лет)
        if (character.age > 80) {
            val deathChance = (character.age - 80) * 0.1 // в 81 год - 10%, в 90 - 100%
            if (Math.random() < deathChance) {
                _gameOverState.update { character.age }
            }
        }
    }

    // Функция, чтобы экран мог сообщить, что переход на GameOverScreen выполнен
    fun onGameOverScreenNavigated() {
        _gameOverState.update { null }
    }
    fun changeJob(newJob: com.example.lifesaga.data.Job) { // Убедись, что тип указан верно
        val currentCharacter = _characterState.value
        if (currentCharacter != null) {
            _characterState.update {
                it?.copy(currentJob = newJob)
            }
        }
    }
    fun quitJob() {
        _characterState.update { character ->
            // Просто убираем текущую работу
            character?.copy(currentJob = null)
        }
    }
}

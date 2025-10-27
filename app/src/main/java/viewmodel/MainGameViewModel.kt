package com.example.lifesaga.viewmodel

import androidx.lifecycle.ViewModel
// Явные и точные импорты вместо "звёздочки"
import com.example.lifesaga.data.Character
import com.example.lifesaga.data.EventChoice
import com.example.lifesaga.data.EventRepository
import com.example.lifesaga.data.GameEvent
import com.example.lifesaga.data.Job
import com.example.lifesaga.data.NewsRepository
import com.example.lifesaga.data.SchoolAction
// Остальные импорты без изменений
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class MainGameViewModel : ViewModel() {

    private val _characterState = MutableStateFlow<Character?>(null)
    val characterState = _characterState.asStateFlow()

    // Для событий, требующих ВЫБОРА игрока (диалоговое окно)
    private val _currentEvent = MutableStateFlow<GameEvent?>(null)
    val currentEvent = _currentEvent.asStateFlow()

    // Список текстовых событий за год для лога на главном экране
    private val _yearEventsLog = MutableStateFlow<List<String>>(emptyList())
    val yearEventsLog = _yearEventsLog.asStateFlow()

    private val _gameOverState = MutableStateFlow<Int?>(null)
    val gameOverState = _gameOverState.asStateFlow()

    fun setInitialCharacter(character: Character) {
        _characterState.value = character
        _gameOverState.value = null
        _yearEventsLog.value = listOf("Начало новой жизни...")
    }

    fun nextYear() {
        val character = _characterState.value ?: return

        val newYearLog = mutableListOf<String>()

        val income = character.currentJob?.salary ?: if (character.age >= 18) 500 else 0
        if (income > 0) {
            newYearLog.add("Годовой доход: $income $")
        }

        if (Random.nextDouble() < 0.3) {
            val news = NewsRepository.getRandomNews()
            newYearLog.add("[НОВОСТИ]: $news")
        }

        // 1. Рассчитываем НОВОЕ состояние персонажа (с новым возрастом и деньгами)
        val updatedCharacter = character.copy(
            age = character.age + 1,
            money = character.money + income
        )

        // 2. СРАЗУ ЖЕ применяем это новое состояние. Теперь возраст ГАРАНТИРОВАННО обновится.
        _characterState.value = updatedCharacter

        // 3. Теперь, уже на основе обновленного персонажа, ищем интерактивное событие
        val interactiveEvent = EventRepository.getRandomEvent(updatedCharacter)
        if (interactiveEvent != null) {
            // Если событие есть — показываем его. Персонаж уже постарел.
            _currentEvent.value = interactiveEvent
        } else {
            // Если события нет — просто проверяем на конец игры.
            checkGameOver(updatedCharacter)
        }

        // 4. Обновляем лог для UI в самом конце
        _yearEventsLog.value = newYearLog
    }

    fun handleEventChoice(choice: EventChoice) {
        val currentCharacter = _characterState.value ?: return
        // Применяем эффект от выбора к УЖЕ обновленному персонажу
        val characterAfterChoice = choice.action(currentCharacter)
        _characterState.value = characterAfterChoice
        _currentEvent.value = null // Закрываем диалог
        checkGameOver(characterAfterChoice)
    }

    fun handleSchoolAction(action: SchoolAction) {
        _characterState.value?.let { currentCharacter ->
            if (currentCharacter.age >= 18) return@let

            val logMessage: String
            val updatedCharacter: Character = when (action) {
                SchoolAction.STUDY_HARD -> {
                    logMessage = "Вы усердно учились весь год."
                    currentCharacter.copy(
                        smarts = (currentCharacter.smarts + 2).coerceAtMost(100),
                        schoolPerformance = (currentCharacter.schoolPerformance + 5).coerceAtMost(100),
                        happiness = (currentCharacter.happiness - 3).coerceAtLeast(0)
                    )
                }
                SchoolAction.SLACK_OFF -> {
                    logMessage = "Вы отдыхали и почти не появлялись в школе."
                    currentCharacter.copy(
                        happiness = (currentCharacter.happiness + 5).coerceAtMost(100),
                        schoolPerformance = (currentCharacter.schoolPerformance - 8).coerceAtLeast(0)
                    )
                }
            }
            // Сбрасываем предыдущий лог и показываем только действие игрока
            _yearEventsLog.value = listOf(logMessage)
            _characterState.value = updatedCharacter
            // Сразу запускаем следующий год
            nextYear()
        }
    }

    fun changeJob(newJob: Job) {
        val logMessage = "Вы устроились на новую работу: ${newJob.title}."
        _characterState.update { it?.copy(currentJob = newJob) } // Правильно присваиваем работу
        _yearEventsLog.value = listOf(logMessage)
        nextYear()
    }

    fun quitJob() {
        val logMessage = "Вы уволились с работы."
        _characterState.update { it?.copy(currentJob = null) }
        _yearEventsLog.value = listOf(logMessage)
        nextYear()
    }

    private fun checkGameOver(character: Character) {
        if (character.health <= 0) {
            _gameOverState.value = character.age
            return
        }
        if (character.age > 80) {
            if (Random.nextDouble() < (character.age - 80) * 0.1) {
                _gameOverState.value = character.age
            }
        }
    }

    fun onGameOverScreenNavigated() {
        _gameOverState.value = null
    }
}

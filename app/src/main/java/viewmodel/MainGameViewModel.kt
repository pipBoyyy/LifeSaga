package com.example.lifesaga.viewmodel

import androidx.compose.ui.unit.coerceAtMost
import androidx.core.app.Person
import androidx.lifecycle.ViewModel
import com.example.lifesaga.data.Asset // Убедись, что этот импорт правильный
import com.example.lifesaga.data.Character
import com.example.lifesaga.data.EventChoice
import com.example.lifesaga.data.EventRepository
import com.example.lifesaga.data.GameEvent
import com.example.lifesaga.data.Job
import com.example.lifesaga.data.NewsRepository
import com.example.lifesaga.data.SchoolAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random
import com.example.lifesaga.data.PersonRepository
import com.example.lifesaga.data.Relationship

class MainGameViewModel : ViewModel() {

    private val _personToInteract = MutableStateFlow<com.example.lifesaga.data.Person?>(null)
    val personToInteract = _personToInteract.asStateFlow()

    fun selectPersonToInteract(person: com.example.lifesaga.data.Person) {
        _personToInteract.value = person
    }

    fun clearPersonToInteract() {
        _personToInteract.value = null
    }

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

        // 1. Расходы на содержание имущества
        val totalAnnualCost = character.assets.sumOf { it.annualCost }
        var moneyAfterCosts = character.money
        if (totalAnnualCost > 0) {
            moneyAfterCosts -= totalAnnualCost
            newYearLog.add("Расходы на имущество: -$totalAnnualCost $")
        }

        // 2. Доход (используем деньги уже ПОСЛЕ вычета расходов)
        val income = character.currentJob?.salary ?: if (character.age >= 18) 500 else 0
        if (income > 0) {
            newYearLog.add("Годовой доход: +$income $")
        }
        val finalMoney = moneyAfterCosts + income

        // 3. Мировые новости
        if (Random.nextDouble() < 0.3) {
            val news = NewsRepository.getRandomNews()
            newYearLog.add("[НОВОСТИ]: $news")
        }

        // 4. Обновляем персонажа со всеми изменениями за год
        val updatedCharacter = character.copy(
            age = character.age + 1,
            money = finalMoney // Используем финальную сумму денег
        )

        // 5. СРАЗУ ЖЕ применяем это новое состояние
        _characterState.value = updatedCharacter

        // 6. Теперь, на основе обновленного персонажа, ищем интерактивное событие
        val interactiveEvent = EventRepository.getRandomEvent(updatedCharacter)
        if (interactiveEvent != null) {
            // Если событие есть — показываем его.
            _currentEvent.value = interactiveEvent
        } else {
            // Если события нет — просто проверяем на конец игры.
            checkGameOver(updatedCharacter)
        }

        // 7. Обновляем лог для UI в самом конце
        _yearEventsLog.value = newYearLog
    }

    fun createNewCharacter(name: String) {
        val newCharacter = Character(
            name = name,
            age = 1,
            health = 100,
            happiness = 75,
            money = 50,
            smarts = 50,
            schoolPerformance = 60,
            currentJob = null,
            assets = emptyList()
        )
        setInitialCharacter(newCharacter)
        val parents = PersonRepository.getInitialParents()
        parents.forEach { parent ->
            val initialRelationship = Relationship(
                personId = parent.id,
                relationshipMeter = Random.nextInt(70, 95) // Начальные отношения с родителями
            )
            newCharacter.relationships.add(initialRelationship)
        }
    }

    fun resetGame() {
        _characterState.value = null
        _gameOverState.value = null
        _yearEventsLog.value = emptyList()
        _currentEvent.value = null
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
        _characterState.update { it?.copy(currentJob = newJob) }
        _yearEventsLog.value = listOf(logMessage)
        nextYear()
    }

    fun quitJob() {
        val logMessage = "Вы уволились с работы."
        _characterState.update { it?.copy(currentJob = null) }
        _yearEventsLog.value = listOf(logMessage)
        nextYear()
    }

    fun buyAsset(asset: Asset) {
        _characterState.value?.let { character ->
            if (character.money >= asset.price) {
                val updatedAssets = character.assets + asset
                val updatedCharacter = character.copy(
                    money = character.money - asset.price,
                    happiness = (character.happiness + asset.happinessBoost).coerceAtMost(100),
                    assets = updatedAssets
                )
                _characterState.value = updatedCharacter
                // Добавляем сообщение о покупке в лог
                _yearEventsLog.value = listOf("Вы купили: ${asset.name}!")
            } else {
                // Сообщение о нехватке денег
                _yearEventsLog.value = listOf("Недостаточно денег для покупки '${asset.name}'!")
            }
        }
        // Запускаем следующий год после попытки покупки
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

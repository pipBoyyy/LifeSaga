package com.example.lifesaga.viewmodel

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
import kotlin.ranges.coerceIn
import kotlin.ranges.randomOrNull
import com.example.lifesaga.data.InteractionResult
import kotlinx.coroutines.flow.StateFlow
import com.example.lifesaga.data.ActionResult // <-- ДОБАВЬ ЭТУ СТРОКУ

class MainGameViewModel : ViewModel() {

    private val _personToInteract = MutableStateFlow<com.example.lifesaga.data.Person?>(null)
    val personToInteract = _personToInteract.asStateFlow()

    fun selectPersonToInteract(person: com.example.lifesaga.data.Person) {
        _personToInteract.value = person
    }

    private val _interactionResult = MutableStateFlow<InteractionResult?>(null)
    val interactionResult: StateFlow<InteractionResult?> = _interactionResult

    fun clearInteractionResult() {
        _interactionResult.value = null
    }

    fun interactWithPerson(action: String) {
        val currentChar = _characterState.value ?: return
        val person = _personToInteract.value ?: return

        // Определяем стоимость действия в энергии
        val energyCost = when (action) {
            "talk" -> 15
            "compliment" -> 20
            "argue" -> 35
            else -> 0
        }

        // 1. Проверяем, достаточно ли энергии
        if (currentChar.energy < energyCost) {
            // Если энергии не хватает, создаем специальный результат и выходим
            _interactionResult.value = InteractionResult(
                message = "У вас слишком мало энергии для этого действия.",
                relationshipChange = 0,
                happinessChange = 0
            )
            // Закрываем диалог выбора персонажа, чтобы показать диалог с результатом
            clearPersonToInteract()
            return
        }

        // 2. Определяем возможные исходы для каждого действия
        val outcomes = when (action) {
            "talk" -> listOf(
                InteractionResult("У вас была теплая, уютная и непринужденная беседа.", relationshipChange = 3, happinessChange = 2),
                InteractionResult("Разговор был немного неловким, но вы нашли общие темы.", relationshipChange = 1, happinessChange = 0),
                InteractionResult("Вы поспорили о пустяках, и остался неприятный осадок.", relationshipChange = -2, happinessChange = -3),
                InteractionResult("Вы отлично поладили и долго смеялись.", relationshipChange = 4, happinessChange = 3)
            )
            "compliment" -> listOf(
                InteractionResult("Ваш комплимент был принят с благодарностью, человек явно смутился.", relationshipChange = 5, happinessChange = 2),
                InteractionResult("Комплимент прозвучал немного неуклюже, но его оценили.", relationshipChange = 2, happinessChange = 1),
                InteractionResult("Человек не понял вашего комплимента и посмотрел на вас с подозрением.", relationshipChange = -3, happinessChange = -2),
                InteractionResult("Вы попали в самое сердце! Отношения заметно потеплели.", relationshipChange = 7, happinessChange = 3)
            )
            "argue" -> listOf(
                InteractionResult("Вы яростно поспорили, но в итоге пришли к общему мнению. Уважение выросло.", relationshipChange = 2, happinessChange = -1),
                InteractionResult("Спор перерос в настоящую ссору. Вы наговорили друг другу лишнего.", relationshipChange = -10, happinessChange = -8),
                InteractionResult("Вы пытались спорить, но вас быстро поставили на место. Вы чувствуете себя глупо.", relationshipChange = -5, happinessChange = -5),
                InteractionResult("Ваши аргументы были настолько убедительны, что вы вышли из спора победителем.", relationshipChange = 1, happinessChange = 4)
            )
            else -> emptyList()
        }

        // 3. Выбираем случайный исход
        val result = outcomes.randomOrNull() ?: return

        // 4. Обновляем состояние персонажа
        val updatedRelationships = currentChar.relationships.toMutableList()
        val relIndex = updatedRelationships.indexOfFirst { it.personId == person.id }
        if (relIndex != -1) {
            val oldRel = updatedRelationships[relIndex]
            updatedRelationships[relIndex] = oldRel.copy(
                relationshipMeter = (oldRel.relationshipMeter + result.relationshipChange).coerceIn(0, 100)
            )
        }

        _characterState.value = currentChar.copy(
            relationships = updatedRelationships,
            happiness = (currentChar.happiness + result.happinessChange).coerceIn(0, 100),
            energy = (currentChar.energy - energyCost).coerceAtLeast(0) // Тратим энергию
        )

        // 5. Сохраняем результат для отображения в UI и закрываем старый диалог
        _interactionResult.value = result
        clearPersonToInteract()
    }


    fun clearPersonToInteract() {
        _personToInteract.value = null
    }

    fun goToHospital() {
        _characterState.value?.let { currentChar ->
            val cost = 250
            val healthGain = 8

            if (currentChar.money >= cost && currentChar.health < 100) {
                // Обновляем персонажа
                _characterState.value = currentChar.copy(
                    money = currentChar.money - cost,
                    health = (currentChar.health + healthGain).coerceAtMost(100)
                )
                // Создаем сообщение для диалога
                val messages = listOf(
                    "Врачи вас осмотрели и подлатали. Вы чувствуете себя лучше!",
                    "Вы прошли курс процедур. Здоровье заметно улучшилось.",
                    "Поход в больницу не прошел даром, вы полны сил.",
                    "Доктор прописал вам витамины и отдых. Ваше самочувствие улучшилось.",
                    "Вы успешно прошли обследование. Все показатели в норме!"
                )
                _actionResult.value = ActionResult(messages.random())
            }
        }
    }

    fun doSport() {
        _characterState.value?.let { currentChar ->
            val energyCost = 30
            val fitnessGain = 1

            if (currentChar.energy >= energyCost) {
                // Обновляем персонажа
                _characterState.value = currentChar.copy(
                    energy = currentChar.energy - energyCost,
                    fitness = (currentChar.fitness + fitnessGain).coerceAtMost(10)
                )
                // Создаем сообщение для диалога
                val messages = listOf(
                    "Отличная тренировка! Вы чувствуете прилив сил.",
                    "Вы выжали из себя все соки в спортзале. Мышцы приятно гудят.",
                    "После интенсивной пробежки мир кажется ярче.",
                    "Вы побили свой личный рекорд в жиме лежа. Так держать!",
                    "Тренер похвалил вас за усердие. Результат не заставит себя ждать."
                )
                _actionResult.value = ActionResult(messages.random())
            }
        }
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

    private val _actionResult = MutableStateFlow<ActionResult?>(null)
    val actionResult: StateFlow<ActionResult?> = _actionResult
// ▲▲▲

    // Метод для закрытия диалога
    fun clearActionResult() {
        _actionResult.value = null
    }


    fun setInitialCharacter(character: Character) {
        _characterState.value = character
        _gameOverState.value = null
        _yearEventsLog.value = listOf("Начало новой жизни...")
    }

    fun nextYear() {
        val character = _characterState.value ?: return

        // Создаем ЕДИНЫЙ лог для всех событий этого года
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

        // ▼▼▼ БЛОК С РАСЧЕТОМ ЗДОРОВЬЯ ▼▼▼
        var healthLoss = 0
        if (character.age >= 50) {
            val baseLoss = when {
                character.fitness >= 7 -> 2 // Высокий фитнес
                character.fitness >= 4 -> 3 // Средний фитнес
                else -> 5                   // Низкий фитнес
            }
            val additionalLoss = (character.age - 50) / 2
            healthLoss = baseLoss + additionalLoss

            // СРАЗУ ДОБАВЛЯЕМ СООБЩЕНИЕ В ЛОГ ЭТОГО ГОДА
            if (healthLoss > 0) {
                newYearLog.add("Возраст дает о себе знать. Здоровье снизилось на $healthLoss.")
            }
        }
        // ▲▲▲

        // 4. Обновляем персонажа со всеми изменениями за год
        val updatedCharacter = character.copy(
            age = character.age + 1,
            money = finalMoney,
            energy = 100,
            health = (character.health - healthLoss).coerceAtLeast(0) // Применяем потерю здоровья
        )

        // 5. СРАЗУ ЖЕ применяем это новое состояние
        _characterState.value = updatedCharacter

        // 6. Теперь, на основе обновленного персонажа, ищем интерактивное событие
        val interactiveEvent = EventRepository.getRandomEvent(updatedCharacter)
        if (interactiveEvent != null) {
            _currentEvent.value = interactiveEvent
        } else {
            // Если события нет — просто проверяем на конец игры.
            checkGameOver(updatedCharacter) // Передаем обновленного персонажа
        }

        // 7. Обновляем лог для UI в самом конце, ОДИН РАЗ
        _yearEventsLog.value = newYearLog
    }

    fun createNewCharacter(name: String) {
        val initialRelationships = mutableListOf<Relationship>()
        val parents = PersonRepository.getInitialParents()
        parents.forEach { parent ->
            val initialRelationship = Relationship(
                personId = parent.id,
                relationshipMeter = Random.nextInt(70, 95)
            )
            initialRelationships.add(initialRelationship)
        }
        val newCharacter = Character(
            name = name,
            age = 6,
            health = 100,
            happiness = 75,
            energy = 100,
            money = 50,
            smarts = 50,
            fitness = 3,
            schoolPerformance = 60,
            currentJob = null,
            assets = emptyList(),
            hasGymMembership = false,
            relationships = initialRelationships
        )
        setInitialCharacter(newCharacter)

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

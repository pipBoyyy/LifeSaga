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
import com.example.lifesaga.data.EducationLevel
import com.example.lifesaga.ui.composables.PostSchoolChoice
import com.example.lifesaga.data.University // Убедись, что этот импорт есть
import com.example.lifesaga.navigation.Screen // <-- ДОБАВЬ ЭТУ СТРОКУ
import com.example.lifesaga.data.PartTimeJob
import com.example.lifesaga.data.UniversityEventRepository
import com.example.lifesaga.data.UniversityRepository
import com.example.lifesaga.viewmodel.UniversityAction



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

    fun doPartTimeJob(job: PartTimeJob) {
        _characterState.value?.let { currentChar ->
            // Проверяем, достаточно ли энергии для выполнения работы
            if (currentChar.energy >= job.energyCost) {
                _characterState.value = currentChar.copy(
                    energy = currentChar.energy - job.energyCost, // Тратим энергию
                    money = currentChar.money + job.moneyGain      // Получаем деньги
                )
                // Отправляем сообщение о результате в диалоговое окно
                _actionResult.value = ActionResult("Вы поработали как '${job.name}' и получили ${job.moneyGain}$.")
            } else {
                // Этот блок сработает, если что-то пойдет не так (хотя UI должен блокировать нажатие)
                _actionResult.value = ActionResult("У вас не хватило энергии на эту подработку.")
            }
        }
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

    fun enrollInUniversity(university: University) {
        _characterState.value?.let { character ->
            // Проверяем, достаточно ли денег на оплату первого года
            if (character.money >= university.tuitionFee) {
                val updatedCharacter = character.copy(
                    money = character.money - university.tuitionFee, // Списываем деньги
                    universityId = university.id, // Запоминаем, куда поступили
                    yearsInUniversity = 1, // Начинаем с первого курса
                    currentJob = null // Увольняемся с работы, если она была
                )
                _characterState.value = updatedCharacter

                _yearEventsLog.value = listOf(
                    "Поздравляем! Вы поступили в '${university.name}'.",
                    "Оплачен первый год обучения: -${university.tuitionFee}$"
                )
                // Сразу переходим в следующий год, чтобы начать учебу
                nextYear()
            } else {
                // Этот блок на случай, если логика в UI даст сбой (хотя кнопка должна быть неактивна)
                _yearEventsLog.value = listOf("Недостаточно денег для поступления.")
            }
        }
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
        val currentCharacter = _characterState.value ?: return
        val newYearLog = mutableListOf<String>()

        // ▼▼▼ НОВЫЙ БЛОК: ЛОГИКА ОБУЧЕНИЯ В УНИВЕРСИТЕТЕ ▼▼▼
        // Эта проверка должна идти в самом начале, т.к. студенческая жизнь заменяет обычную.
        if (currentCharacter.universityId != null && currentCharacter.education != EducationLevel.UNIVERSITY_DEGREE) {
            val university = UniversityRepository.getUniversityById(currentCharacter.universityId)
            if (university != null) {
                newYearLog.add("Вы продолжаете обучение в '${university.name}'. Курс: ${currentCharacter.yearsInUniversity + 1}.")

                // 1. Проверка оплаты
                if (currentCharacter.money < university.tuitionFee) {
                    newYearLog.add("[ГРУСТНО]: У вас не хватило денег (${university.tuitionFee}$) на оплату обучения. Вас отчислили!")
                    _characterState.value = currentCharacter.copy(
                        age = currentCharacter.age + 1,
                        universityId = null, // Теряем университет
                        yearsInUniversity = 0,
                        energy = 100
                    )
                    _yearEventsLog.value = newYearLog
                    return // Прерываем выполнение. Год для студента закончен (отчислением).
                }

                // 2. Оплата и прогресс
                val moneyAfterTuition = currentCharacter.money - university.tuitionFee
                newYearLog.add("Оплачен год обучения: -${university.tuitionFee}$.")
                val newYearsInUniversity = currentCharacter.yearsInUniversity + 1

                // 3. Проверка на окончание университета
                if (newYearsInUniversity >= university.yearsToComplete) {
                    newYearLog.add("🎉 ПОЗДРАВЛЯЕМ! Вы окончили университет и получили диплом!")
                    _characterState.value = currentCharacter.copy(
                        age = currentCharacter.age + 1,
                        money = moneyAfterTuition,
                        education = EducationLevel.UNIVERSITY_DEGREE, // Получаем диплом!
                        universityId = null,
                        yearsInUniversity = 0,
                        energy = 100
                    )
                } else {
                    // Если учеба продолжается
                    _characterState.value = currentCharacter.copy(
                        age = currentCharacter.age + 1,
                        money = moneyAfterTuition,
                        yearsInUniversity = newYearsInUniversity,
                        energy = 100
                    )
                }

                _yearEventsLog.value = newYearLog
                // Год для студента закончен. Пропускаем остальную логику (работа, спортзал и т.д.)
                return
            }
        }
        // ▲▲▲ КОНЕЦ БЛОКА ПРО УНИВЕРСИТЕТ ▲▲▲


        // --- ДАЛЬШЕ ИДЕТ ТВОЯ СУЩЕСТВУЮЩАЯ ЛОГИКА ДЛЯ НЕ-СТУДЕНТОВ ---

        var updatedCharacter = currentCharacter.copy()

        // 0. ТВОЯ ЛОГИКА АБОНЕМЕНТА В СПОРТЗАЛ
        if (updatedCharacter.hasGymMembership) {
            val gymAnnualCost = 1000
            if (updatedCharacter.money >= gymAnnualCost) {
                updatedCharacter = updatedCharacter.copy(
                    money = updatedCharacter.money - gymAnnualCost,
                    fitness = (updatedCharacter.fitness + 1).coerceAtMost(10)
                )
                newYearLog.add("Вы продлили абонемент в спортзал за $gymAnnualCost$. Ваша форма улучшается.")
            } else {
                updatedCharacter = updatedCharacter.copy(hasGymMembership = false)
                newYearLog.add("У вас не хватило денег на продление абонемента. Он аннулирован.")
            }
        } else {
            if (updatedCharacter.age % 2 == 0 && updatedCharacter.fitness > 0) {
                updatedCharacter = updatedCharacter.copy(
                    fitness = (updatedCharacter.fitness - 1).coerceAtLeast(0)
                )
                newYearLog.add("Вы теряете физическую форму. Показатель 'Спорт' снизился.")
            }
        }

        // 1. Расходы на имущество
        val totalAnnualCost = updatedCharacter.assets.sumOf { it.annualCost }
        var moneyAfterCosts = updatedCharacter.money
        if (totalAnnualCost > 0) {
            moneyAfterCosts -= totalAnnualCost
            newYearLog.add("Расходы на имущество: -$totalAnnualCost $")
        }

        // 2. Доход
        val income = updatedCharacter.currentJob?.salary ?: if (updatedCharacter.age >= 18) 500 else 0
        if (income > 0) {
            newYearLog.add("Годовой доход: +$income $")
        }
        val finalMoney = moneyAfterCosts + income

        // 3. Мировые новости
        if (Random.nextDouble() < 0.3) {
            val news = NewsRepository.getRandomNews()
            newYearLog.add("[НОВОСТИ]: $news")
        }

        // Уведомление о подработке в 14 лет
        if (currentCharacter.age + 1 == 14) {
            newYearLog.add("[ВАЖНО]: Вам исполнилось 14 лет! Теперь вам доступна подработка в меню 'Действия'.")
        }

        // Расчет потери здоровья
        var healthLoss = 0
        if (updatedCharacter.age >= 50) {
            val baseLoss = when {
                updatedCharacter.fitness >= 7 -> 2
                updatedCharacter.fitness >= 4 -> 3
                else -> 5
            }
            val additionalLoss = (updatedCharacter.age - 50) / 2
            healthLoss = baseLoss + additionalLoss
            if (healthLoss > 0) {
                newYearLog.add("Возраст дает о себе знать. Здоровье снизилось на $healthLoss.")
            }
        }

        // 4. Финальное обновление персонажа
        val finalUpdatedCharacter = updatedCharacter.copy(
            age = updatedCharacter.age + 1,
            money = finalMoney,
            energy = 100,
            health = (updatedCharacter.health - healthLoss).coerceAtLeast(0)
        )
        _characterState.value = finalUpdatedCharacter

        // 5. Поиск интерактивного события
        val interactiveEvent = EventRepository.getRandomEvent(finalUpdatedCharacter)
        if (interactiveEvent != null) {
            _currentEvent.value = interactiveEvent
        } else {
            checkGameOver(finalUpdatedCharacter)
        }

        // 6. Обновление лога
        _yearEventsLog.value = newYearLog
    }

    fun createNewCharacter(name: String) {
        val initialRelationships = mutableListOf<Relationship>()
        val parents = PersonRepository.getInitialParents()
        parents.forEach { parent ->
            val initialRelationship = Relationship(
                personId = parent.id, // parent.id теперь String, и personId тоже String. Все сходится!
                relationshipMeter = Random.nextInt(70, 95)
            )
            initialRelationships.add(initialRelationship)
        }
        val newCharacter = Character(
            name = name,
            gender = "Мужской",
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
            relationships = initialRelationships,
            education = EducationLevel.NONE,
            universityId = null, // <-- ДОБАВЬ ЭТУ СТРОКУ
            yearsInUniversity = 0  // <-- И ЭТУ СТРОКУ
        )
        setInitialCharacter(newCharacter)

    }

    fun resetGame() {
        _characterState.value = null
        _gameOverState.value = null
        _yearEventsLog.value = emptyList()
        _currentEvent.value = null
    }

    private val _showPostSchoolChoiceDialog = MutableStateFlow(false)
    val showPostSchoolChoiceDialog = _showPostSchoolChoiceDialog.asStateFlow()


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
            var updatedCharacter: Character = currentCharacter

            when (action) {
                SchoolAction.STUDY_HARD -> {
                    logMessage = "Вы усердно учились весь год."
                    updatedCharacter = currentCharacter.copy(
                        smarts = (currentCharacter.smarts + 2).coerceAtMost(100),
                        schoolPerformance = (currentCharacter.schoolPerformance + 5).coerceAtMost(100),
                        happiness = (currentCharacter.happiness - 3).coerceAtLeast(0)
                    )
                }
                SchoolAction.SLACK_OFF -> {
                    logMessage = "Вы отдыхали и почти не появлялись в школе."
                    updatedCharacter = currentCharacter.copy(
                        happiness = (currentCharacter.happiness + 5).coerceAtMost(100),
                        schoolPerformance = (currentCharacter.schoolPerformance - 8).coerceAtLeast(0)
                    )
                }
            }

            // ПРОВЕРКА НА ОКОНЧАНИЕ ШКОЛЫ
            if (updatedCharacter.age == 17) {
                // Это последний год. Выдаем диплом и готовимся показать диалог.
                updatedCharacter = updatedCharacter.copy(education = EducationLevel.HIGH_SCHOOL)
                _characterState.value = updatedCharacter // Сохраняем диплом

                // Вместо вызова nextYear(), мы активируем флаг для показа диалога
                _showPostSchoolChoiceDialog.value = true

                _yearEventsLog.value = listOf(logMessage, "Вы окончили школу!")
            } else {
                // Если школа не окончена, все как обычно
                _characterState.value = updatedCharacter
                _yearEventsLog.value = listOf(logMessage)
                nextYear() // Переходим в следующий год
            }
        }
    }
    fun handleUniversityAction(action: UniversityAction) {
        _characterState.value?.let { currentCharacter ->
            // Проверяем, что персонаж действительно студент
            if (currentCharacter.universityId == null) return@let

            var updatedCharacter = currentCharacter
            val eventLog = mutableListOf<String>()

            when (action) {
                UniversityAction.GO_TO_PARTY -> {
                    if (updatedCharacter.energy < 35) {
                        // Если не хватает энергии, можно показать сообщение (пока просто выходим)
                        return@let
                    }
                    updatedCharacter = updatedCharacter.copy(
                        energy = updatedCharacter.energy - 35,
                        happiness = (updatedCharacter.happiness + 15).coerceAtMost(100),
                        smarts = (updatedCharacter.smarts - 2).coerceAtLeast(0)
                    )
                    eventLog.add(UniversityEventRepository.getRandomPartyEvent())

                    // Добавляем 2 новых знакомых
                    val newPeople = PersonRepository.generateRandomPeople(2)
                    PersonRepository.addPeople(newPeople)
                    val newRelationships = newPeople.map { person ->
                        Relationship(personId = person.id, relationshipMeter = Random.nextInt(15, 40))
                    }
                    updatedCharacter.relationships.addAll(newRelationships)
                    eventLog.add("Вы познакомились с новыми людьми: ${newPeople.joinToString { it.name }}.")

                    // Шанс 25% на потерю девственности (если еще не было)
                    if (!updatedCharacter.lostVirginity && Random.nextDouble() < 0.25) {
                        updatedCharacter = updatedCharacter.copy(lostVirginity = true)
                        eventLog.add("❤️ Этой ночью произошло нечто особенное. Вы лишились девственности!")
                    }
                }

                UniversityAction.STUDY_HARD -> {
                    if (updatedCharacter.energy < 25) {
                        return@let
                    }
                    var smartsBoost = 5
                    // Шанс 10% на буст интеллекта
                    if (Random.nextDouble() < 0.10) {
                        smartsBoost = 10
                        eventLog.add("💡 ВАУ! Вы блестяще справились с тестом, ваш интеллект резко вырос!")
                    }
                    updatedCharacter = updatedCharacter.copy(
                        energy = updatedCharacter.energy - 25,
                        happiness = (updatedCharacter.happiness - 5).coerceAtLeast(0),
                        smarts = (updatedCharacter.smarts + smartsBoost).coerceAtMost(100)
                    )
                    eventLog.add(UniversityEventRepository.getRandomStudyHardEvent())
                }

                UniversityAction.MEET_PEOPLE -> {
                    if (updatedCharacter.energy < 15) {
                        return@let
                    }
                    updatedCharacter = updatedCharacter.copy(
                        energy = updatedCharacter.energy - 15,
                        happiness = (updatedCharacter.happiness + 5).coerceAtMost(100)
                    )
                    eventLog.add(UniversityEventRepository.getRandomMeetPeopleEvent())

                    // Добавляем 1 нового знакомого
                    val newPerson = PersonRepository.generateRandomPeople(1).first()
                    PersonRepository.addPerson(newPerson)
                    val newRelationship = Relationship(personId = newPerson.id, relationshipMeter = Random.nextInt(20, 50))
                    updatedCharacter.relationships.add(newRelationship)
                    eventLog.add("В вашем списке контактов пополнение: ${newPerson.name}.")
                }

                UniversityAction.SKIP_CLASSES -> {
                    updatedCharacter = updatedCharacter.copy(
                        energy = (updatedCharacter.energy + 15).coerceAtMost(100),
                        happiness = (updatedCharacter.happiness + 5).coerceAtMost(100),
                        smarts = (updatedCharacter.smarts - 5).coerceAtLeast(0)
                    )
                    eventLog.add(UniversityEventRepository.getRandomSkipClassesEvent())
                }
            }

            // Применяем все изменения к персонажу
            _characterState.value = updatedCharacter
            // Передаем все накопившиеся за действие сообщения в лог и...
            _yearEventsLog.value = eventLog
            // ...сразу же переходим на следующий год!
            nextYear()
        }
    }
    fun handlePostSchoolChoice(choice: PostSchoolChoice, navigate: (String) -> Unit) {
        // Сначала скрываем диалог
        _showPostSchoolChoiceDialog.value = false

        when (choice) {
            PostSchoolChoice.UNIVERSITY -> {
                // Вызываем навигацию на экран поступления
                navigate(Screen.Enrollment.route)
            }
            PostSchoolChoice.WORK -> {
                // Вызываем навигацию на экран выбора работы
                navigate(Screen.Jobs.route)
            }
            PostSchoolChoice.ARMY -> {
                // Логика для армии в будущем
                _yearEventsLog.value = listOf("Призывная комиссия рассмотрит вашу заявку в следующем году.")
                nextYear()
            }
        }
    }

    // Также нужен метод для сброса диалога, если пользователь его просто закрыл (хотя мы этого не позволяем)
    fun dismissPostSchoolChoiceDialog() {
        _showPostSchoolChoiceDialog.value = false
        // Можно решить, что делать дальше. Например, просто перейти на следующий год.
        _yearEventsLog.value = listOf("Вы решили взять год на размышления.")
        nextYear()
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

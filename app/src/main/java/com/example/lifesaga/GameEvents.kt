package com.example.lifesaga

import kotlin.random.Random

// --- Структуры для событий ---
data class GameEventOutcome(
    val description: String,
    val healthChange: Int = 0,
    val happinessChange: Int = 0,
    val moneyChange: Int = 0,
    val moneyChangePercent: Double = 0.0,
    val healthChangePercent: Double = 0.0,
    val happinessChangePercent: Double = 0.0,
    val setEducationLevel: EducationLevel? = null,
    val setHasChronicDisease: Boolean? = null,
    val customAction: ((Character) -> Character)? = null
)

data class EventChoice(
    val description: String,
    val outcome: GameEventOutcome
)

data class GameEvent(
    val id: String,
    val eventText: String,
    val directOutcome: GameEventOutcome? = null, // Если событие не требует выбора
    val choices: List<EventChoice>? = null,     // Если событие требует выбора
    // Условие, при котором событие может произойти. Возвращает true/false.
    val condition: (character: Character, currentYear: Int) -> Boolean,
    val isMandatory: Boolean = false // Если true, событие происходит обязательно, если условия соблюдены
)

// --- ГЛАВНЫЙ ОБЪЕКТ, СОДЕРЖАЩИЙ ВСЕ СОБЫТИЯ ---
object GameEvents {
    val events = listOf(
        // НОВОЕ СОБЫТИЕ 1: Случайная встреча
        GameEvent(
            id = "random_encounter",
            eventText = "Вы случайно встретили старого знакомого, которого давно не видели. Что будете делать?",
            choices = listOf(
                EventChoice(
                    "Поболтать и вспомнить былое",
                    outcome = GameEventOutcome(
                        description = "Приятная беседа улучшила ваше настроение.",
                        happinessChange = 10
                    )
                ),
                EventChoice(
                    "Спешить по своим делам, лишь поздоровавшись",
                    outcome = GameEventOutcome(
                        description = "Вы были заняты, но чувствовали себя немного виноватым.",
                        happinessChange = -2
                    )
                )
            ),
            condition = { char, _ -> char.age >= 10 && Random.nextDouble() < 0.07 }, // 7% шанс
            isMandatory = false
        ),

        // НОВОЕ СОБЫТИЕ 4: Спортивное достижение
        GameEvent(
            id = "sport_achievement",
            eventText = "Вы приняли участие в местном спортивном соревновании и показали отличный результат!",
            directOutcome = GameEventOutcome(
                description = "Ваши усилия окупились, вы чувствуете себя сильным и счастливым.",
                healthChange = 5,
                happinessChange = 10
            ),
            condition = { char, _ -> char.health > 50 && char.age >= 10 && char.age < 60 && Random.nextDouble() < 0.08 }, // 8% шанс
            isMandatory = false
        ),

        // НОВОЕ СОБЫТИЕ 5: Поломка техники
        GameEvent(
            id = "tech_breakdown",
            eventText = "Ваш важный бытовой прибор (например, холодильник или машина) сломался и требует ремонта.",
            directOutcome = GameEventOutcome(
                description = "Неприятная поломка, требующая незапланированных расходов.",
                moneyChange = -Random.nextInt(80, 250),
                happinessChange = -5
            ),
            condition = { char, _ -> char.money > 100 && char.age >= 20 && Random.nextDouble() < 0.10 }, // 10% шанс
            isMandatory = false
        ),

        // НОВОЕ СОБЫТИ 6: Помощь другу
        GameEvent(
            id = "help_friend",
            eventText = "Ваш друг попал в беду и просит о помощи. Вы можете помочь ему деньгами или советом.",
            choices = listOf(
                EventChoice(
                    "Помочь деньгами (если есть)",
                    outcome = GameEventOutcome(
                        description = "Вы помогли другу финансово. Он очень благодарен, но вы немного потратились.",
                        moneyChange = -Random.nextInt(50, 150),
                        happinessChange = 10
                    )
                ),
                EventChoice(
                    "Предложить моральную поддержку и совет",
                    outcome = GameEventOutcome(
                        description = "Вы поддержали друга. Он ценит вашу дружбу, и ваше счастье немного выросло.",
                        happinessChange = 5
                    )
                )
            ),
            condition = { char, _ -> char.age >= 15 && Random.nextDouble() < 0.07 }, // 7% шанс
            isMandatory = false
        ),

        // НОВОЕ СОБЫТИ 7: Новое хобби
        GameEvent(
            id = "new_hobby",
            eventText = "Вы обнаружили новое интересное хобби, которое очень вас увлекло!",
            directOutcome = GameEventOutcome(
                description = "Новое хобби приносит вам много радости и немного улучшает здоровье.",
                happinessChange = 10,
                healthChange = 3,
                moneyChange = -Random.nextInt(20, 80) // Расходы на хобби
            ),
            condition = { char, _ -> char.happiness < 80 && char.age >= 10 && Random.nextDouble() < 0.06 }, // 6% шанс, если счастье не на максимуме
            isMandatory = false
        ),

        // НОВОЕ СОБЫТИЕ 8: Общественная деятельность
        GameEvent(
            id = "community_work",
            eventText = "В вашем районе проходит акция по благоустройству или волонтерству. Вы решите присоединиться?",
            choices = listOf(
                EventChoice(
                    "Принять активное участие",
                    outcome = GameEventOutcome(
                        description = "Вы внесли свой вклад в общество. Чувство удовлетворения и уважение окружающих.",
                        happinessChange = 15
                    )
                ),
                EventChoice(
                    "Пройти мимо",
                    outcome = GameEventOutcome(
                        description = "Вы решили не участвовать. Ваша жизнь не изменилась.",
                        happinessChange = 0
                    )
                )
            ),
            condition = { char, _ -> char.age >= 18 && Random.nextDouble() < 0.09 }, // 9% шанс
            isMandatory = false
        ),

        // НОВОЕ СОБЫТИЕ 9: Несчастный случай
        GameEvent(
            id = "accident",
            eventText = "Вы попали в небольшой несчастный случай. К счастью, ничего серьезного, но вы получили ушибы.",
            directOutcome = GameEventOutcome(
                description = "Неприятный инцидент, который немного повлиял на ваше здоровье и настроение.",
                healthChange = -15,
                happinessChange = -10,
                moneyChange = -Random.nextInt(30, 100) // Медицинские расходы
            ),
            condition = { char, _ -> char.health > 20 && char.age >= 5 && Random.nextDouble() < 0.04 }, // 4% шанс
            isMandatory = false
        ),

        // НОВОЕ СОБЫТИЕ 10: Путешествие
        GameEvent(
            id = "travel_opportunity",
            eventText = "У вас появилась возможность отправиться в небольшое путешествие. Это будет стоить денег, но принесет новые впечатления.",
            choices = listOf(
                EventChoice(
                    "Отправиться в путешествие",
                    outcome = GameEventOutcome(
                        description = "Вы отлично провели время в путешествии! Это стоило денег, но оно того стоило.",
                        happinessChange = 25,
                        moneyChange = -Random.nextInt(200, 600),
                        healthChange = 5
                    )
                ),
                EventChoice(
                    "Отказаться, сэкономить деньги",
                    outcome = GameEventOutcome(
                        description = "Вы решили остаться дома и сэкономить. Возможно, вы упустили что-то интересное.",
                        happinessChange = -5
                    )
                )
            ),
            condition = { char, _ -> char.money > 500 && char.age >= 18 && Random.nextDouble() < 0.05 }, // 5% шанс
            isMandatory = false
        ),
        // ОБЯЗАТЕЛЬНОЕ СОБЫТИЕ: Первый день в школе (в 6 лет)
        GameEvent(
            id = "first_school_day",
            eventText = "Сегодня ваш первый день в школе! Вы чувствуете лёгкое волнение. Как вы проведёте этот день?",
            choices = listOf(
                EventChoice(
                    "Активно участвовать во всех уроках",
                    outcome = GameEventOutcome(
                        description = "Вы активно участвовали в уроках. Образование повышено до начальной школы.",
                        happinessChange = 5,
                        customAction = { char ->
                            char.copy(educationLevel = EducationLevel.PRIMARY_SCHOOL)
                        }
                    )
                ),
                EventChoice(
                    "Играть с друзьями на перемене",
                    outcome = GameEventOutcome(
                        description = "Вы отлично провели время. Но школа начата: образование = начальная школа.",
                        happinessChange = 10,
                        customAction = { char ->
                            char.copy(educationLevel = EducationLevel.PRIMARY_SCHOOL)
                        }
                    )
                ),
                EventChoice(
                    "Сидеть тихо в углу и читать книгу",
                    outcome = GameEventOutcome(
                        description = "Вы почитали. Образование повышено до начальной школы, но счастье упало.",
                        happinessChange = -3,
                        customAction = { char ->
                            char.copy(educationLevel = EducationLevel.PRIMARY_SCHOOL)
                        }
                    )
                )
            ),
            condition = { character, _ ->
                character.age == 6 && character.educationLevel == EducationLevel.NONE
            },
            isMandatory = true
        ),

        // ОБЯЗАТЕЛЬНОЕ СОБЫТИЕ: Переход в среднюю школу (в 11 лет)
        GameEvent(
            id = "middle_school_start",
            eventText = "Вы заканчиваете начальную школу и переходите в среднюю. Как вы подойдёте к этому этапу?",
            choices = listOf(
                EventChoice(
                    "Сосредоточиться на учёбе",
                    outcome = GameEventOutcome(
                        description = "Вы усердно учитесь. Образование = средняя школа, но стресс снизил счастье.",
                        happinessChange = -5,
                        customAction = { char ->
                            char.copy(educationLevel = EducationLevel.MIDDLE_SCHOOL)
                        }
                    )
                ),
                EventChoice(
                    "Заводить друзей и отдыхать",
                    outcome = GameEventOutcome(
                        description = "Вы классно проводите время. Образование = средняя школа, счастье растёт.",
                        happinessChange = 10,
                        customAction = { char ->
                            char.copy(educationLevel = EducationLevel.MIDDLE_SCHOOL)
                        }
                    )
                )
            ),
            condition = { character, _ ->
                character.age == 11 && character.educationLevel == EducationLevel.PRIMARY_SCHOOL
            },
            isMandatory = true
        ),

        // ОБЯЗАТЕЛЬНОЕ СОБЫТИЕ: Переход в старшую школу (в 15 лет)
        GameEvent(
            id = "high_school_start",
            eventText = "Средняя школа позади, впереди старшие классы! Что выберете?",
            choices = listOf(
                EventChoice(
                    "Готовиться к вузу",
                    outcome = GameEventOutcome(
                        description = "Вы учитесь на отлично. Образование = старшая школа, но стресс и здоровье упали.",
                        happinessChange = -10,
                        healthChange = -3,
                        customAction = { char ->
                            char.copy(educationLevel = EducationLevel.HIGH_SCHOOL)
                        }
                    )
                ),
                EventChoice(
                    "Наслаждаться жизнью",
                    outcome = GameEventOutcome(
                        description = "Вы кайфуете. Образование = старшая школа, счастье растёт, но потрачены деньги.",
                        happinessChange = 15,
                        moneyChange = -20,
                        customAction = { char ->
                            char.copy(educationLevel = EducationLevel.HIGH_SCHOOL)
                        }
                    )
                )
            ),
            condition = { character, _ ->
                character.age == 15 && character.educationLevel == EducationLevel.MIDDLE_SCHOOL
            },
            isMandatory = true
        ),

        // ОБЯЗАТЕЛЬНОЕ СОБЫТИЕ: Выбор после старшей школы (в 18 лет)
        GameEvent(
            id = "post_high_school_choice",
            eventText = "Вы закончили старшую школу! Что дальше? Пора принимать важные решения о своем будущем.",
            choices = listOf(
                EventChoice(
                    "Поступить в университет",
                    outcome = GameEventOutcome(
                        description = "Вы решили продолжить образование. Впереди годы учебы, но и большие перспективы.",
                        happinessChange = 5,
                        customAction = { char -> char.copy(educationLevel = EducationLevel.BACHELOR, money = char.money - 500) } // Плата за поступление
                    )
                ),
                EventChoice(
                    "Сразу пойти работать",
                    outcome = GameEventOutcome(
                        description = "Вы решили начать работать. Это принесет немедленный доход, но, возможно, ограничит карьерный рост.",
                        moneyChange = 200, // Начальный заработок
                        happinessChange = 10,
                        customAction = { char -> char.copy(currentJob = Job.ENTRY_LEVEL, jobStatus = JobStatus.EMPLOYED) }
                    )
                ),
                EventChoice(
                    "Взять год перерыва",
                    outcome = GameEventOutcome(
                        description = "Вы решили взять год на размышления. Это может быть полезно для самоопределения.",
                        happinessChange = 15,
                        healthChange = 5,
                        customAction = { char -> char.copy(money = char.money - 100) } // Небольшие расходы на жизнь
                    )

                )
            ),
            condition = { character, _ -> character.age == 18 && character.educationLevel == EducationLevel.HIGH_SCHOOL },
            isMandatory = true
        ),

        // Пример события "Болезнь"
        GameEvent(
            id = "sickness",
            eventText = "Вы почувствовали себя плохо, у вас высокая температура. Что вы будете делать?",
            choices = listOf(
                EventChoice(
                    "Пойти к врачу",
                    outcome = GameEventOutcome(
                        description = "Вы обратились к врачу. Лечение помогло, но это стоило денег.",
                        healthChange = 20,
                        moneyChange = -50,
                        happinessChange = -5
                    )
                ),
                EventChoice(
                    "Само пройдет, отлежусь",
                    outcome = GameEventOutcome(
                        description = "Вы решили перетерпеть. Здоровье значительно ухудшилось, и вы чувствуете себя слабее.",
                        healthChange = -30,
                        happinessChange = -15
                    )
                )
            ),
            condition = { character, _ -> character.health < 60 && Random.nextDouble() < 0.15 && character.age >= 10 }, // Низкое здоровье и случайный шанс
            isMandatory = false
        ),

        // Пример события "Нашли деньги"
        GameEvent(
            id = "found_money",
            eventText = "Гуляя по улице, вы случайно нашли на земле кошелек! Что будете делать?",
            choices = listOf(
                EventChoice(
                    "Отнести в полицию/бюро находок",
                    outcome = GameEventOutcome(
                        description = "Вы поступили честно. Потерянный кошелек был возвращен владельцу, и вы чувствуете себя очень хорошо.",
                        happinessChange = 15
                    )
                ),
                EventChoice(
                    "Забрать себе",
                    outcome = GameEventOutcome(
                        description = "Вы решили оставить кошелек себе. Деньги улучшили ваше финансовое положение, но совесть немного грызет.",
                        moneyChange = Random.nextInt(50, 200),
                        happinessChange = -5
                    )
                )
            ),
            condition = { _, _ -> Random.nextDouble() < 0.05 }, // 5% шанс каждый год
            isMandatory = false
        ),

        // Пример события "Повышение на работе"
        GameEvent(
            id = "job_promotion",
            eventText = "Ваш начальник предложил вам повышение! Это означает больше ответственности, но и более высокую зарплату.",
            directOutcome = GameEventOutcome(
                description = "Поздравляем! Вы получили повышение. Зарплата увеличилась, но и стресса стало больше.",
                moneyChangePercent = 0.20,
                happinessChange = -5, // Повышение может принести стресс
                customAction = { char -> char.copy(jobStatus = JobStatus.EMPLOYED) } // Просто убедимся, что он трудоустроен
            ),
            condition = { character, _ -> character.jobStatus == JobStatus.EMPLOYED && Random.nextDouble() < 0.1 && character.age >= 25 },
            isMandatory = false
        ),


        // Пример события "Кризис среднего возраста"
        GameEvent(
            id = "midlife_crisis",
            eventText = "Вы достигли среднего возраста и начинаете переосмысливать свою жизнь. Вы чувствуете себя немного подавленным.",
            directOutcome = GameEventOutcome(
                description = "Вы переживаете кризис среднего возраста. Чувство подавленности временно снижает ваше счастье.",
                happinessChange = -15
            ),
            condition = { character, _ -> character.age in 40..50 && Random.nextDouble() < 0.2 },
            isMandatory = false
        ),

        // Пример события "Выход на пенсию"
        GameEvent(
            id = "retirement",
            eventText = "Вы достигли пенсионного возраста! Пришло время попрощаться с работой и наслаждаться заслуженным отдыхом.",
            directOutcome = GameEventOutcome(
                description = "Вы вышли на пенсию. Теперь у вас больше свободного времени, но доход уменьшился.",
                moneyChangePercent = -0.30, // Снижение дохода после выхода на пенсию
                happinessChange = 10,
                customAction = { char -> char.copy(currentJob = null, jobStatus = JobStatus.UNEMPLOYED) } // Увольняемся
            ),
            condition = { character, _ -> character.age >= 60 && character.jobStatus == JobStatus.EMPLOYED && Random.nextDouble() < 0.5 },
            isMandatory = true // Высокий шанс, если условия подходят
        ),

        // Пример события "Серьезная болезнь" (для низкого здоровья)
        GameEvent(
            id = "serious_illness",
            eventText = "Ваше здоровье резко ухудшилось, вы чувствуете себя очень плохо. Это может быть серьезно.",
            choices = listOf(
                EventChoice(
                    "Обратиться к лучшим специалистам (дорого)",
                    outcome = GameEventOutcome(
                        description = "Вы получили лучшее лечение. Здоровье улучшилось, но вы понесли большие расходы.",
                        healthChange = Random.nextInt(20, 40),
                        moneyChange = -Random.nextInt(100, 300),
                        setHasChronicDisease = if (Random.nextDouble() < 0.1) true else null // Шанс хронической болезни
                    )
                ),
                EventChoice(
                    "Надеяться на естественное выздоровление",
                    outcome = GameEventOutcome(
                        description = "Вы решили не обращаться за помощью. Болезнь сильно подорвала ваше здоровье.",
                        healthChange = -Random.nextInt(25, 50),
                        happinessChange = -Random.nextInt(10, 20),
                        setHasChronicDisease = if (Random.nextDouble() < 0.5) true else null
                    )
                )
            ),
            condition = { character, _ ->
                character.health < 40 && !character.hasChronicDisease && character.age >= 10 && Random.nextDouble() < 0.1
            },
            isMandatory = false
        ),
        GameEvent(
            id = "found_lost_item",
            eventText = "Вы нашли давно потерянную вещь! Какая удача!",
            directOutcome = GameEventOutcome(
                description = "Настроение сразу улучшилось!",
                happinessChange = Random.nextInt(5,15),
                moneyChange = Random.nextInt(0,30) // Может, это была заначка
            ),
            condition = { _, _ -> Random.nextDouble() < 0.03 }, // Редкое случайное событие
            isMandatory = false
        ),
        GameEvent(
            id = "study_hard",
            eventText = "У вас есть возможность пройти курсы повышения квалификации или получить дополнительное образование.",
            choices = listOf(
                EventChoice(
                    "Записаться на курсы",
                    outcome = GameEventOutcome(
                        description = "Вы успешно прошли курсы. Ваши навыки улучшились, что откроет новые возможности.",
                        happinessChange = 5,
                        moneyChange = -50, // Стоимость курсов
                        customAction = { char -> char.copy(educationLevel = char.educationLevel.nextLevel()) } // Переход на следующий уровень образования
                    )
                ),
                EventChoice(
                    "Отказаться, сэкономить деньги",
                    outcome = GameEventOutcome(
                        description = "Вы решили не тратиться на обучение. Ваши навыки остались прежними.",
                        happinessChange = -2 // Немного жаль упущенной возможности
                    )
                )
            ),
            condition = { char, _ -> char.age >= 18 && char.educationLevel != EducationLevel.PHD && Random.nextDouble() < 0.1 },
            isMandatory = false
        ),
        GameEvent(
            id = "relationship_opportunity",
            eventText = "Вы встретили человека, который вам очень понравился. Есть шанс начать новые отношения!",
            choices = listOf(
                EventChoice(
                    "Начать отношения",
                    outcome = GameEventOutcome(
                        description = "Вы начали новые отношения. Ваша жизнь стала ярче, но требует времени и усилий.",
                        happinessChange = 15,
                        customAction = { char -> char.copy(relationshipStatus = RelationshipStatus.IN_RELATIONSHIP) }
                    )
                ),
                EventChoice(
                    "Остаться одному",
                    outcome = GameEventOutcome(
                        description = "Вы решили остаться одиноким. Ваша жизнь не изменилась.",
                        happinessChange = 0
                    )
                )
            ),
            condition = { char, _ -> char.age >= 18 && char.relationshipStatus == RelationshipStatus.SINGLE && Random.nextDouble() < 0.1 },
            isMandatory = false
        ),

        GameEvent(
            id = "military_service",
            eventText = "Вам пришла повестка в армию! Вам предстоит принять решение.",
            choices = listOf(
                EventChoice(
                    "Отслужить в армии",
                    outcome = GameEventOutcome(
                        description = "Вы успешно прошли военную службу. Это был нелегкий год, но вы стали сильнее.",
                        healthChange = 10,
                        happinessChange = -10, // Может быть стрессом
                        customAction = { char -> char.copy(completedMilitaryService = true, currentJob = null, jobStatus = JobStatus.UNEMPLOYED) } // Увольняемся перед армией
                    )
                ),
                EventChoice(
                    "Попробовать откосить (рискованно)",
                    outcome = GameEventOutcome(
                        description = "Вы попытались избежать службы. Либо получилось, либо нет...",
                        customAction = { char ->
                            if (Random.nextDouble() < 0.5) { // 50% шанс успеха
                                char.copy(happiness = char.happiness + 5) // Успех
                            } else {
                                char.copy(happiness = char.happiness - 20, money = char.money - 200) // Неудача, штраф
                            }
                        }
                    )
                )
            ),
            condition = { char, _ -> char.age == 18 && char.gender == Gender.MALE && !char.completedMilitaryService && Random.nextDouble() < 0.3 },
            isMandatory = true
        ),
        // НОВОЕ СОБЫТИЕ: Поиск работы после университета/перерыва
        GameEvent(
            id = "job_search_post_education",
            eventText = "Вы закончили обучение (или перерыв) и теперь ищете работу. Какое направление выберете?",
            choices = listOf(
                EventChoice(
                    "Искать высокооплачиваемую работу (сложно)",
                    outcome = GameEventOutcome(
                        description = "Вы нацелились на высокую зарплату. Это может занять время, но обещает хорошие доходы.",
                        moneyChange = 0,
                        happinessChange = -5, // Стресс от поиска
                        customAction = { char -> char.copy(currentJob = Job.HIGH_PAYING, jobStatus = JobStatus.EMPLOYED) }
                    )
                ),
                EventChoice(
                    "Искать стабильную работу со средним доходом",
                    outcome = GameEventOutcome(
                        description = "Вы выбрали стабильность. Работа найдена быстрее, доход средний.",
                        moneyChange = 0,
                        happinessChange = 5,
                        customAction = { char -> char.copy(currentJob = Job.MEDIUM_PAYING, jobStatus = JobStatus.EMPLOYED) }
                    )
                ),
                EventChoice(
                    "Пока не работать, продолжить саморазвитие",
                    outcome = GameEventOutcome(
                        description = "Вы решили пока не работать, а посвятить себя саморазвитию. Это может окупиться в будущем.",
                        moneyChange = -100, // Расходы на жизнь
                        happinessChange = 10,
                        customAction = { char -> char.copy(educationLevel = char.educationLevel.nextLevel()) } // Возможно, улучшаем образование
                    )
                )
            ),
            condition = { char, _ ->
                char.age >= 19 && char.jobStatus == JobStatus.UNEMPLOYED &&
                        (char.educationLevel == EducationLevel.BACHELOR || char.educationLevel == EducationLevel.HIGH_SCHOOL)
            },
            isMandatory = true // Обязательно, если персонаж без работы после учебы/перерыва
        ),
        // НОВОЕ СОБЫТИЕ: Неожиданные расходы
        GameEvent(
            id = "unexpected_expense",
            eventText = "Что-то сломалось дома, внезапный счет! Это неожиданные расходы.",
            directOutcome = GameEventOutcome(
                description = "Непредвиденные расходы ударили по вашему бюджету.",
                moneyChange = -Random.nextInt(50, 300),
                happinessChange = -5
            ),
            condition = { char, _ -> char.money > 100 && Random.nextDouble() < 0.15 && char.age >= 20 },
            isMandatory = false
        ),
        // НОВОЕ СОБЫТИЕ: Наследство
        GameEvent(
            id = "inheritance",
            eventText = "Вы получили неожиданное наследство от дальнего родственника!",
            directOutcome = GameEventOutcome(
                description = "Наследство значительно улучшило ваше финансовое положение.",
                moneyChange = Random.nextInt(1000, 5000),
                happinessChange = 20
            ),
            condition = { _, _ -> Random.nextDouble() < 0.01 }, // Очень редкое событие
            isMandatory = false
        ),
        // НОВОЕ СОБЫТИЕ: Покупка недвижимости
        GameEvent(
            id = "buy_property",
            eventText = "У вас достаточно денег, чтобы купить недвижимость. Это крупное вложение!",
            choices = listOf(
                EventChoice(
                    "Купить скромный дом/квартиру",
                    outcome = GameEventOutcome(
                        description = "Вы стали владельцем недвижимости. Это дает стабильность, но требует вложений.",
                        moneyChange = -Random.nextInt(5000, 15000),
                        happinessChange = 10
                    )
                ),
                EventChoice(
                    "Инвестировать деньги, не покупать",
                    outcome = GameEventOutcome(
                        description = "Вы решили инвестировать, а не покупать. Это может принести прибыль в будущем.",
                        moneyChange = -Random.nextInt(2000, 8000),
                        moneyChangePercent = 0.05, // Небольшой бонус к будущим доходам
                        happinessChange = 5
                    )
                )
            ),
            condition = { char, _ -> char.money > 10000 && char.age >= 25 && Random.nextDouble() < 0.05 },
            isMandatory = false

        )


    )
}


// Расширяющая функция для удобства перехода на следующий уровень образования
fun EducationLevel.nextLevel(): EducationLevel {
    return when (this) {
        EducationLevel.NONE -> EducationLevel.PRIMARY_SCHOOL
        EducationLevel.PRIMARY_SCHOOL -> EducationLevel.MIDDLE_SCHOOL
        EducationLevel.MIDDLE_SCHOOL -> EducationLevel.HIGH_SCHOOL
        EducationLevel.HIGH_SCHOOL -> EducationLevel.BACHELOR
        EducationLevel.BACHELOR -> EducationLevel.MASTER
        EducationLevel.MASTER -> EducationLevel.PHD
        EducationLevel.PHD -> EducationLevel.PHD // После PHD дальше некуда
    }
}
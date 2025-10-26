package com.example.lifesaga.data

object EventRepository {

    // --- ИЗМЕНЯЕМ ЭТУ ФУНКЦИЮ ---
    // Теперь она принимает текущего персонажа, чтобы фильтровать события
    fun getRandomEvent(character: Character): GameEvent? {
        // 1. Отбираем только те события, для которых выполняется условие (или у которых его нет)
        val availableEvents = allEvents.filter { event ->
            event.condition == null || event.condition.invoke(character)
        }
        // 2. Возвращаем случайное из доступных, или null, если таких нет
        return availableEvents.randomOrNull()
    }
    // -----------------------------

    private val allEvents = listOf(
        // --- ОБНОВЛЯЕМ СТАРЫЕ СОБЫТИЯ ---
        GameEvent(
            title = "Находка на улице",
            description = "Прогуливаясь по парку, вы замечаете на земле кошелек. Внутри 500$.",
            choices = listOf(
                EventChoice(
                    description = "Забрать деньги себе",
                    action = { it.copy(money = it.money + 500, happiness = (it.happiness - 5).coerceAtLeast(0)) } // -5 счастья за нечестность
                ),
                EventChoice(
                    description = "Сдать в полицию",
                    action = { it.copy(happiness = (it.happiness + 10).coerceAtMost(100)) } // +10 счастья за честность
                )
            )
        ),
        GameEvent(
            title = "Приступ лени",
            description = "Целую неделю вы чувствовали себя не в своей тарелке и почти не выходили из дома.",
            choices = listOf(
                EventChoice(
                    description = "Бывает...",
                    action = { it.copy(health = (it.health - 5).coerceAtLeast(0), happiness = (it.happiness - 5).coerceAtLeast(0)) }
                )
            )
        ),

        // --- ДОБАВЛЯЕМ НОВЫЕ СОБЫТИЯ С УСЛОВИЯМИ ---
        GameEvent(
            title = "Предложение учиться",
            description = "Вас приглашают на престижные курсы повышения квалификации. Это стоит 2000$.",
            // Условие: возраст от 22 до 40 лет И есть деньги на учебу
            condition = { it.age in 22..40 && it.money >= 2000 },
            choices = listOf(
                EventChoice(
                    description = "Инвестировать в себя",
                    action = { it.copy(money = it.money - 2000, smarts = (it.smarts + 15).coerceAtMost(100)) } // -деньги, +ум
                ),
                EventChoice(
                    description = "Слишком дорого",
                    action = { it } // Ничего не меняется
                )
            )
        ),
        GameEvent(
            title = "Кризис среднего возраста",
            description = "Вы смотрите в зеркало и понимаете, что молодость прошла. Внезапно захотелось купить спортивную машину.",
            // Условие: возраст от 40 до 50 лет
            condition = { it.age in 40..50 },
            choices = listOf(
                EventChoice(
                    description = "Это просто минутная слабость",
                    action = { it.copy(happiness = (it.happiness - 10).coerceAtLeast(0)) } // -счастье
                )
            )
        ),
        GameEvent(
            title = "Счастливый лотерейный билет",
            description = "Вы выиграли в лотерею 10 000$!",
            // Условие: нет
            choices = listOf(
                EventChoice(
                    description = "Вот это удача!",
                    action = { it.copy(money = it.money + 10000, happiness = (it.happiness + 20).coerceAtMost(100)) }
                )
            )
        ),
        GameEvent(
            title = "Вас уволили!",
            description = "Ваш начальник говорит, что вы не справляетесь со своими обязанностями. Вы уволены.",
            // Условие: есть работа И ум ниже 30
            condition = { it.currentJob != null && it.smarts < 30 },
            choices = listOf(
                EventChoice(
                    description = "Вот и славно!",
                    action = { character -> character.copy(currentJob = null, happiness = (character.happiness + 5).coerceAtMost(100)) }
                )
            )
        ),

        GameEvent(
            title = "Повышение!",
            description = "Ваш босс впечатлен вашей работой и предлагает вам повышение!",
            // Условие: есть работа с возможностью повышения И ум > 60 И счастье > 60
            condition = {
                it.currentJob?.promotionTo != null && it.smarts > 60 && it.happiness > 60
            },
            choices = listOf(
                EventChoice(
                    description = "Принять предложение",
                    action = { character ->
                        // Находим новую должность в репозитории
                        val newJob = JobRepository.findJobByTitle(character.currentJob!!.promotionTo!!)
                        // Обновляем работу и повышаем счастье
                        character.copy(currentJob = newJob, happiness = (character.happiness + 15).coerceAtMost(100))
                    }
                ),
                EventChoice(
                    description = "Отказаться",
                    action = { character -> character.copy(happiness = (character.happiness - 10).coerceAtLeast(0)) }
                )
            )
        )


    )

}

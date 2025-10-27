package com.example.lifesaga.data

object EventRepository {

    fun getRandomEvent(character: Character): GameEvent? {
        // 1. Отбираем только те события, для которых выполняется условие (или у которых его нет)
        val availableEvents = allEvents.filter { event ->
            event.condition == null || event.condition.invoke(character)
        }
        // 2. Возвращаем случайное из доступных, или null, если таких нет
        return availableEvents.randomOrNull()
    }

    private val allEvents = listOf(
        // --- СОБЫТИЯ ПРИВЕДЕНЫ К ЕДИНОМУ ФОРМАТУ ---

        // Событие 1: Находка
        GameEvent(
            description = "Вы нашли на улице кошелек. Внутри 200$.",
            choices = listOf(
                EventChoice(
                    text = "Взять деньги себе",
                    action = { it.copy(money = it.money + 200, happiness = it.happiness + 5) }
                ),
                EventChoice(
                    text = "Отнести в полицию",
                    action = { it.copy(happiness = (it.happiness + 10).coerceAtMost(100)) } // +счастье за честность
                )
            )
        ),

        // Событие 2: Болезнь
        GameEvent(
            description = "Вы сильно простудились и провели неделю в постели.",
            condition = { it.health < 80 }, // Появляется, только если здоровье не идеальное
            choices = listOf(
                EventChoice(
                    text = "Ничего страшного",
                    action = { it.copy(health = (it.health - 10).coerceAtLeast(0)) }
                )
            )
        ),

        // Событие 3: Удача
        GameEvent(
            description = "Ваш дальний родственник оставил вам небольшое наследство.",
            condition = { it.age > 21 }, // Только для взрослых
            choices = listOf(
                EventChoice(
                    text = "Отлично!",
                    action = { it.copy(money = it.money + 1000) }
                )
            )
        ),

        // Событие 4: Социальное
        GameEvent(
            description = "Вы поссорились с лучшим другом.",
            choices = listOf(
                EventChoice(
                    text = "Это было неизбежно",
                    action = { it.copy(happiness = (it.happiness - 15).coerceAtLeast(0)) }
                )
            )
        ),

        // Событие 5: Предложение учиться
        GameEvent(
            description = "Вас приглашают на престижные курсы повышения квалификации. Это стоит 2000$.",
            condition = { it.age in 22..40 && it.money >= 2000 },
            choices = listOf(
                EventChoice(
                    text = "Инвестировать в себя",
                    action = { it.copy(money = it.money - 2000, smarts = (it.smarts + 15).coerceAtMost(100)) }
                ),
                EventChoice(
                    text = "Слишком дорого",
                    action = { it } // Ничего не меняется
                )
            )
        ),

        // Событие 6: Кризис среднего возраста
        GameEvent(
            description = "Вы смотрите в зеркало и понимаете, что молодость прошла. Внезапно захотелось купить спорткар.",
            condition = { it.age in 40..50 },
            choices = listOf(
                EventChoice(
                    text = "Это просто минутная слабость",
                    action = { it.copy(happiness = (it.happiness - 10).coerceAtLeast(0)) }
                )
            )
        ),

        // Событие 7: Вас уволили!
        GameEvent(
            description = "Ваш начальник говорит, что вы не справляетесь со своими обязанностями. Вы уволены.",
            condition = { it.currentJob != null && it.smarts < 30 },
            choices = listOf(
                EventChoice(
                    text = "Вот и славно!",
                    action = { character -> character.copy(currentJob = null, happiness = (character.happiness + 5).coerceAtMost(100)) }
                )
            )
        ),

        // Событие 8: Повышение!
        GameEvent(
            description = "Ваш босс впечатлен вашей работой и предлагает вам повышение!",
            // Условие: есть работа с возможностью повышения И ум > 60
            condition = { it.currentJob?.promotionTo != null && it.smarts > 60 },
            choices = listOf(
                EventChoice(
                    text = "Принять предложение",
                    action = { character ->
                        // Находим новую должность в репозитории
                        val newJob = JobRepository.findJobByTitle(character.currentJob!!.promotionTo!!)
                        // Обновляем работу и повышаем счастье
                        character.copy(currentJob = newJob, happiness = (character.happiness + 15).coerceAtMost(100))
                    }
                ),
                EventChoice(
                    text = "Отказаться",
                    action = { character -> character.copy(happiness = (character.happiness - 10).coerceAtLeast(0)) }
                )
            )
        )
    )
}

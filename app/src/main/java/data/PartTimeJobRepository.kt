// В файле: app/src/main/java/com/example/lifesaga/data/PartTimeJobRepository.kt

package com.example.lifesaga.data

object PartTimeJobRepository {

    private val allPartTimeJobs = listOf(
        PartTimeJob(
            id = "promoter",
            name = "Раздавать листовки",
            description = "Махать руками и улыбаться прохожим. Не требует особых навыков.",
            energyCost = 20,
            moneyGain = 300
        ),
        PartTimeJob(
            id = "ticket_seller",
            name = "Продавать билеты в кино",
            description = "Сидячая работа, но требует внимания и общения с людьми.",
            energyCost = 30,
            moneyGain = 450
        ),
        PartTimeJob(
            id = "factory_worker",
            name = "Поработать на заводе",
            description = "Тяжелый физический труд, но и оплата соответствующая.",
            energyCost = 40,
            moneyGain = 700
        )
    )

    /**
     * Возвращает все доступные подработки.
     * В будущем можно добавить логику, чтобы некоторые подработки
     * были доступны не сразу.
     */
    fun getAvailablePartTimeJobs(): List<PartTimeJob> {
        return allPartTimeJobs
    }
}
    
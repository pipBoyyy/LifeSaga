package com.example.lifesaga.data

object JobRepository {

    private val allJobs = listOf(
        Job(title = "Дворник", salary = 12000, promotionTo = "Старший дворник"),
        Job(title = "Старший дворник", salary = 18000),

        Job(title = "Курьер", salary = 15000),

        Job(title = "Стажер в офисе", salary = 20000, requiredSmarts = 40, promotionTo = "Офисный работник"),
        Job(title = "Офисный работник", salary = 30000, requiredSmarts = 40, promotionTo = "Менеджер"),
        Job(title = "Менеджер", salary = 50000, requiredSmarts = 50),

        Job(title = "Младший программист", salary = 40000, requiredSmarts = 70, promotionTo = "Программист"),
        Job(title = "Программист", salary = 80000, requiredSmarts = 70, promotionTo = "Старший программист"),
        Job(title = "Старший программист", salary = 120000, requiredSmarts = 80)
    )

    fun getAvailableJobs(character: Character): List<Job> {
        return allJobs.filter { job ->
            character.smarts >= job.requiredSmarts &&
                    character.currentJob?.title != job.title
        }
    }

    // --- ДОБАВЬ ЭТУ ФУНКЦИЮ ---
    // Ищет работу по ее названию
    fun findJobByTitle(title: String): Job? {
        return allJobs.find { it.title == title }
    }
}

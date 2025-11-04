// В файле: data/UniversityRepository.kt
// ПОЛНОСТЬЮ ЗАМЕНИ СОДЕРЖИМОЕ ФАЙЛА

package com.example.lifesaga.data

object UniversityRepository {

    private val allUniversities = listOf(
        University(
            id = "community_college",
            name = "Общественный колледж",
            tuitionFee = 2000,
            requiredSmarts = 40,
            yearsToComplete = 2
        ),
        University(
            id = "state_university",
            name = "Государственный университет",
            tuitionFee = 8000,
            requiredSmarts = 70,
            yearsToComplete = 4
        ),
        University(
            id = "prestige_university",
            name = "Престижный университет Лиги Плюща",
            tuitionFee = 25000,
            requiredSmarts = 90,
            yearsToComplete = 4
        )
    )

    /**
     * Возвращает список университетов, в которые персонаж может поступить,
     * исходя из его уровня ума.
     */
    fun getAvailableUniversities(character: Character): List<University> {
        return allUniversities.filter { university ->
            character.smarts >= university.requiredSmarts
        }
    }

    // ▼▼▼ ВОТ НЕДОСТАЮЩИЙ МЕТОД ▼▼▼
    /**
     * Находит и возвращает университет по его уникальному ID.
     */
    fun getUniversityById(id: String): University? {
        return allUniversities.find { it.id == id }
    }
    // ▲▲▲
}

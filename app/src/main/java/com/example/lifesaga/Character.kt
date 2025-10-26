package com.example.lifesaga

import androidx.annotation.DrawableRes
import com.example.lifesaga.R // Убедитесь, что этот импорт правильный



enum class Job(val displayName: String, val baseSalary: Int) {
    NONE("Безработный", 0),
    ENTRY_LEVEL("Начальная позиция", 500),
    MEDIUM_PAYING("Специалист", 1500),
    HIGH_PAYING("Топ-менеджер", 3000),
    // Добавьте другие профессии по желанию
}



enum class RelationshipStatus {
    SINGLE,
    IN_RELATIONSHIP,
    MARRIED,
    DIVORCED,
    WIDOWED
}




data class Character(
    val name: String,
    val age: Int = 0,
    val gender: Gender,
    @DrawableRes val avatarResId: Int, // ID ресурса для аватара
    val health: Int = 100,
    val happiness: Int = 100,
    val money: Int = 1000,
    val energy: Int = 100,
    val intelligence: Int = 50,
    val educationLevel: EducationLevel = EducationLevel.NONE,
    val currentJob: Job? = null,
    val jobStatus: JobStatus = JobStatus.UNEMPLOYED,
    val relationshipStatus: RelationshipStatus = RelationshipStatus.SINGLE,
    val hasChronicDisease: Boolean = false,
    val completedMilitaryService: Boolean = false,
) {
    /** Восстановление энергии при старте нового года */
    fun recoverEnergy(): Character =
        copy(energy = (energy + 60).coerceAtMost(100))

    /** Прокачка интеллекта (например, при учебе) */
    fun learn(amount: Int): Character =
        copy(intelligence = (intelligence + amount).coerceAtMost(100))

    /** Теперь это вычисляемое свойство, доступное внутри класса */
    val moneyPerYear: Int
        get() = currentJob?.baseSalary ?: 0
}
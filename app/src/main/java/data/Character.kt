// В файле: data/Character.kt
package com.example.lifesaga.data

data class Character(
    val name: String,
    val gender: String, // <--- ВОТ ЭТО ПОЛЕ МЫ ДОБАВИЛИ
    val age: Int = 6,
    val energy: Int,
    val health: Int = 100,
    val money: Int = 0,
    val happiness: Int = 70,
    val smarts: Int = 50,
    val fitness: Int,
    val schoolPerformance: Int = 50,
    val currentJob: Job? = null,
    val assets: List<Asset> = emptyList(),
    val hasGymMembership: Boolean,
    val education: EducationLevel = EducationLevel.NONE,
    // ▼▼▼ ВОТ ГЛАВНОЕ ИЗМЕНЕНИЕ ▼▼▼
    var relationships: MutableList<Relationship> = mutableListOf(),
    // ▲▲▲
    val universityId: String? = null,
    val yearsInUniversity: Int = 0,
    val lostVirginity: Boolean = false
)

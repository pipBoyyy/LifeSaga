// В новом файле data/RelationshipModels.kt
package com.example.lifesaga.data

// Описывает тип отношений
enum class RelationshipType {
    FATHER,
    MOTHER,
    FRIEND,
    PARTNER,
    ENEMY
}

// Описывает конкретного человека в мире игры
data class Person(
    val id: Int,
    val name: String,
    val age: Int,
    val relationshipType: RelationshipType
)

// Описывает отношение нашего игрока к другому человеку
data class Relationship(
    val personId: Int,
    var relationshipMeter: Int, // от 0 до 100
    var romanceMeter: Int? = null // Для романтических партнеров, от 0 до 100
)

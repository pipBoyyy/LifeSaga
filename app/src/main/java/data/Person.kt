// В файле: app/src/main/java/com/example/lifesaga/data/Person.kt

package com.example.lifesaga.data

data class Person(
    val id: String,
    val name: String,
    val age: Int,
    val relationshipType: RelationshipType, // Тип отношений (Отец, Друг)
    val gender: String                      // Пол
)

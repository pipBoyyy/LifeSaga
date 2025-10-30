// В новом файле data/PersonRepository.kt
package com.example.lifesaga.data

object PersonRepository {
    // Пока здесь будут только родители
    private val allPeople = listOf(
        Person(id = 1, name = "Юрий", age = 42, relationshipType = RelationshipType.FATHER),
        Person(id = 2, name = "Анастасия", age = 42, relationshipType = RelationshipType.MOTHER)
    )

    fun getPersonById(id: Int): Person? {
        return allPeople.find { it.id == id }
    }

    fun getInitialParents(): List<Person> {
        return allPeople.filter { it.relationshipType == RelationshipType.FATHER || it.relationshipType == RelationshipType.MOTHER }
    }
}
    
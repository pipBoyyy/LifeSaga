// В файле: data/PersonRepository.kt

package com.example.lifesaga.data

import kotlin.random.Random

object PersonRepository {
    private val allPeople = mutableMapOf<String, Person>()

    // ▼▼▼ ВОТ ИСПРАВЛЕНИЕ ▼▼▼
    private fun addInitialParents() {
        val parents = listOf(
            Person(id = "father", name = "Юрий", age = 42, relationshipType = RelationshipType.FATHER, gender = "Мужской"),
            Person(id = "mother", name = "Анастасия", age = 42, relationshipType = RelationshipType.MOTHER, gender = "Женский")
        )
        parents.forEach { allPeople[it.id] = it }
    }
    // ▲▲▲

    init {
        addInitialParents() // Теперь мы вызываем нашу новую функцию
    }

    // ... (остальной код до reset() остается без изменений) ...
    fun getPersonById(id: String): Person? {
        return allPeople[id]
    }

    fun getInitialParents(): List<Person> {
        return allPeople.values.filter { it.relationshipType == RelationshipType.FATHER || it.relationshipType == RelationshipType.MOTHER }
    }

    fun addPerson(person: Person) {
        if (!allPeople.containsKey(person.id)) {
            allPeople[person.id] = person
        }
    }

    fun addPeople(people: List<Person>) {
        people.forEach { person ->
            addPerson(person)
        }
    }

    fun generateRandomPeople(count: Int): List<Person> {
        val firstNamesMale = listOf("Алекс", "Женя", "Саша", "Макс", "Никита", "Денис", "Иван", "Петр")
        val firstNamesFemale = listOf("Лера", "Оля", "Аня", "Катя", "Мария", "Света", "Даша")
        val lastNames = listOf("Смирнов(а)", "Иванов(а)", "Кузнецов(а)", "Попов(а)", "Волков(а)", "Романов(а)")

        return List(count) {
            val gender = if (Random.nextBoolean()) "Мужской" else "Женский"
            val name = if (gender == "Мужской") firstNamesMale.random() else firstNamesFemale.random()
            val lastName = lastNames.random()

            Person(
                id = "person_${System.currentTimeMillis()}_$it",
                name = "$name $lastName",
                age = Random.nextInt(18, 23),
                relationshipType = RelationshipType.FRIEND,
                gender = gender
            )
        }
    }

    fun reset() {
        allPeople.clear()
        // ▼▼▼ И ЗДЕСЬ ТОЖЕ ВЫЗЫВАЕМ ЕЕ ▼▼▼
        addInitialParents()
        // ▲▲▲
    }
}

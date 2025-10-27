// В файле data/GameEvent.kt

package com.example.lifesaga.data

// Описывает само событие
data class GameEvent(
    val description: String, // Одно поле для описания ситуации
    val condition: ((Character) -> Boolean)? = null, // Условие появления события
    val choices: List<EventChoice> // Список вариантов выбора
)

// Описывает один вариант выбора в событии
data class EventChoice(
    val text: String, // Текст на кнопке, например "Согласиться"
    val action: (Character) -> Character // Функция, которая изменит персонажа
)

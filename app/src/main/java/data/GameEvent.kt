package com.example.lifesaga.data

// Описывает один вариант выбора в событии
data class EventChoice(
    val description: String, // Текст на кнопке, например "Согласиться"
    val action: (Character) -> Character // Функция, которая изменит персонажа
)

// Описывает само событие
data class GameEvent(
    val title: String, // Заголовок события, например "Неожиданное предложение"
    val description: String, // Описание ситуации
    val choices: List<EventChoice>, // Список вариантов выбора)
    val condition: ((Character) -> Boolean)? = null
    // -----------------------
)

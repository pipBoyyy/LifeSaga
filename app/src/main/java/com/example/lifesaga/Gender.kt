package com.example.lifesaga

enum class Gender(val displayName: String) { // Добавляем параметр конструктора
    MALE("Мужской"),
    FEMALE("Женский"),
    UNSPECIFIED("Не указан"); // Точка с запятой, если есть еще что-то в enum

    // Можно добавить companion object или методы, если нужно
}
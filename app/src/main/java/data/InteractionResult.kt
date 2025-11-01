package com.example.lifesaga.data

// Этот data class будет описывать результат любого взаимодействия
data class InteractionResult(
    val message: String, // Сообщение для игрока (например, "У вас была теплая беседа")
    val relationshipChange: Int, // На сколько изменились отношения (+5, -10 и т.д.)
    val happinessChange: Int // На сколько изменилось счастье
)

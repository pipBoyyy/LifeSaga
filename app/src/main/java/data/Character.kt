package com.example.lifesaga.data

data class Character(
    val name: String,
    val age: Int = 18,
    val health: Int = 100,
    val money: Int = 500,
    val happiness: Int = 70, // Счастье от 0 до 100
    val smarts: Int = 50,      // Ум от 0 до 100
    val currentJob: Job? = null
// ---------------------------------
)

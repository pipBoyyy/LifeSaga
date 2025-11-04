// В файле: app/src/main/java/com/example/lifesaga/data/PartTimeJob.kt

package com.example.lifesaga.data

data class PartTimeJob(
    val id: String,
    val name: String,
    val description: String,
    val energyCost: Int, // Сколько энергии тратит
    val moneyGain: Int   // Сколько денег приносит
)
    
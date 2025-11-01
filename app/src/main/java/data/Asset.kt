// В файле data/Asset.kt

package com.example.lifesaga.data

data class Asset(
    val id: String,
    val name: String,
    val price: Int,
    val annualCost: Int, // Ежегодные расходы на содержание
    val happinessBoost: Int,
    val category: AssetCategory,
    val minAge: Int,          // <-- ДОБАВЬ ЭТУ СТРОКУ
    val isUnique: Boolean// Насколько повышает счастье
)

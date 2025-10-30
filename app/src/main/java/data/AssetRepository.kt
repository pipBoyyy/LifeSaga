// В файле: app/src/main/java/com/example/lifesaga/data/AssetRepository.kt

package com.example.lifesaga.data

object AssetRepository {

    private val allAssets = listOf(
        Asset(
            id = "gaming_pc", name = "Игровой ПК", price = 2000,
            annualCost = 50, happinessBoost = 5,
            category = AssetCategory.ELECTRONICS // <-- Добавили
        ),
        Asset(
            id = "used_car", name = "Подержанная машина", price = 5000,
            annualCost = 500, happinessBoost = 8,
            category = AssetCategory.VEHICLES // <-- Добавили
        ),
        Asset(
            id = "new_car", name = "Новая машина", price = 25000,
            annualCost = 1500, happinessBoost = 15,
            category = AssetCategory.VEHICLES // <-- Добавили
        ),
        Asset(
            id = "small_apartment", name = "Маленькая квартира", price = 100000,
            annualCost = 4000, happinessBoost = 25,
            category = AssetCategory.REAL_ESTATE // <-- Добавили
        ),
        Asset(
            id = "luxury_house", name = "Роскошный дом", price = 500000,
            annualCost = 20000, happinessBoost = 50,
            category = AssetCategory.REAL_ESTATE // <-- Добавили
        )
    )

    fun getAvailableAssets(): List<Asset> {
        return allAssets
    }
}

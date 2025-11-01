// В файле: app/src/main/java/com/example/lifesaga/data/AssetRepository.kt

package com.example.lifesaga.data

object AssetRepository {

    private val allAssets = listOf(
        // ▼▼▼ НАШ НОВЫЙ АБОНЕМЕНТ ▼▼▼
        Asset(
            id = "gym_membership",
            name = "Абонемент в спортзал",
            price = 1000,
            annualCost = 1000, // Каждый год нужно платить за продление
            happinessBoost = 5,
            minAge = 16, // Доступен с 16 лет
            isUnique = true, // Можно купить только один раз
            category = AssetCategory.SERVICES // Новая категория "Услуги"
        ),
        // ▲▲▲

        // ЭЛЕКТРОНИКА
        Asset(
            id = "gaming_pc",
            name = "Игровой ПК",
            price = 2000,
            annualCost = 50,
            happinessBoost = 5,
            minAge = 14,
            isUnique = false,
            category = AssetCategory.ELECTRONICS
        ),

        // ТРАНСПОРТ
        Asset(
            id = "used_car",
            name = "Подержанная машина",
            price = 5000,
            annualCost = 500,
            happinessBoost = 8,
            minAge = 18,
            isUnique = false,
            category = AssetCategory.VEHICLES
        ),
        Asset(
            id = "new_car",
            name = "Новая машина",
            price = 25000,
            annualCost = 1500,
            happinessBoost = 15,
            minAge = 18,
            isUnique = false,
            category = AssetCategory.VEHICLES
        ),

        // НЕДВИЖИМОСТЬ
        Asset(
            id = "small_apartment",
            name = "Маленькая квартира",
            price = 100000,
            annualCost = 4000,
            happinessBoost = 25,
            minAge = 21,
            isUnique = false,
            category = AssetCategory.REAL_ESTATE
        ),
        Asset(
            id = "luxury_house",
            name = "Роскошный дом",
            price = 500000,
            annualCost = 20000,
            happinessBoost = 50,
            minAge = 25,
            isUnique = false,
            category = AssetCategory.REAL_ESTATE
        )
    )

    /**
     * Получает список активов, доступных для покупки персонажем.
     * Фильтрует по возрасту и уникальности (если актив уже куплен).
     */
    fun getAvailableAssets(character: Character): List<Asset> {
        val ownedAssetIds = character.assets.map { it.id }.toSet()

        return allAssets.filter { asset ->
            val isOldEnough = character.age >= asset.minAge
            val isNotOwnedYet = !ownedAssetIds.contains(asset.id)

            // Актив доступен, если возраст подходит И (он не уникальный ИЛИ он уникальный, но еще не куплен)
            isOldEnough && (!asset.isUnique || isNotOwnedYet)
        }
    }
}

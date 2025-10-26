package com.example.lifesaga // Убедитесь, что это ваш правильный пакет

import com.example.lifesaga.R // Важно: импортируйте класс R

object DefaultAvatarIds {
    const val NO_AVATAR = 0 // Используйте 0 или другой признак "нет аватара"

    val DEFAULT_MALE_AVATAR = R.drawable.default_male_avatar // <-- Изменили на val
    val DEFAULT_FEMALE_AVATAR = R.drawable.default_female_avatar // <-- Изменили на val
    val GENERAL_DEFAULT_AVATAR = R.drawable.default_avatar // <-- Укажите здесь свой ресурс общего аватара по умолчанию


    val MALE_AVATARS = listOf(
        R.drawable.male_avatar_1, // Убедитесь, что у вас есть эти ресурсы в папке res/drawable
        R.drawable.male_avatar_2,
        R.drawable.default_male_avatar
        // Добавьте больше мужских аватаров, если есть
    )

    val FEMALE_AVATARS = listOf(
        R.drawable.female_avatar_1, // Убедитесь, что у вас есть эти ресурсы в папке res/drawable
        R.drawable.female_avatar_2,
        R.drawable.default_female_avatar
        // Добавьте больше женских аватаров, если есть
    )
}
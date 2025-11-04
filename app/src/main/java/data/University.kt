// В файле: app/src/main/java/com/example/lifesaga/data/University.kt

package com.example.lifesaga.data

data class University(
    val id: String,
    val name: String,
    val tuitionFee: Int, // Стоимость обучения в год
    val requiredSmarts: Int, // Требуемый уровень ума для поступления
    val yearsToComplete: Int // Сколько лет учиться
)
    
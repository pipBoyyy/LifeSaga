package com.example.lifesaga.data

data class Job(
    val title: String,          // Название, н-р "Дворник"
    val salary: Int,            // Зарплата за год
    val requiredSmarts: Int = 0, // Требования к уму для получения работы
    val promotionTo: String? = null
)

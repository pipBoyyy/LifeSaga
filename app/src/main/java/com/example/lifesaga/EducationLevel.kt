package com.example.lifesaga // Убедись, что это твой правильный пакет

enum class EducationLevel(val displayName: String) {
    NONE("Нет образования"),
    PRIMARY_SCHOOL("Начальная школа"),
    MIDDLE_SCHOOL("Средняя школа"),
    HIGH_SCHOOL("Старшая школа/Лицей"),
    BACHELOR("Бакалавриат"),
    MASTER("Магистратура"),
    PHD("Докторантура");
}
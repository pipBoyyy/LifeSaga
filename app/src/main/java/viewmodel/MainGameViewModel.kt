package com.example.lifesaga.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lifesaga.data.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainGameViewModel : ViewModel() {

    // 1. Создаем "хранилище" для нашего персонажа.
    // Оно приватное, чтобы никто извне не мог его случайно изменить.
    private val _characterState = MutableStateFlow<Character?>(null)

    // 2. А это "публичная витрина" нашего хранилища.
    // Экраны будут подписываться на нее, чтобы получать данные.
    val characterState = _characterState.asStateFlow()

    // 3. Функция, которая будет вызываться один раз при старте,
    // чтобы положить нашего созданного персонажа в хранилище.
    fun setInitialCharacter(character: Character) {
        _characterState.update { character }
    }
}

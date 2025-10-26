package com.example.lifesaga

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {

    private val _character = MutableStateFlow<Character?>(null)
    val character: StateFlow<Character?> = _character.asStateFlow()

    private val _currentYear = MutableStateFlow(1960) // Устанавливаем начальный год
    val currentYear: StateFlow<Int> = _currentYear.asStateFlow()

    private val _eventOutcomeMessage = MutableStateFlow<String?>(null)
    val eventOutcomeMessage: StateFlow<String?> = _eventOutcomeMessage.asStateFlow()

    private val _showEventDialog = MutableStateFlow(false)
    val showEventDialog: StateFlow<Boolean> = _showEventDialog.asStateFlow()

    private val _currentEvent = MutableStateFlow<GameEvent?>(null)
    val currentEvent: StateFlow<GameEvent?> = _currentEvent.asStateFlow()

    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()

    // Инициализация персонажа для новой игры
    fun startGame(initialCharacter: Character) {
        _character.value = initialCharacter.copy(age = 0) // Персонаж начинает с 0 лет
        _currentYear.value = 1960 // Убедимся, что год сброшен
        _isGameOver.value = false
        _eventOutcomeMessage.value = null
        _showEventDialog.value = false
        _currentEvent.value = null
        Log.d("GameViewModel", "Игра начата с персонажем: ${initialCharacter.name}")
    }

    fun passYear() {
        if (_isGameOver.value) return

        hideEventDialog()            // _showEventDialog = false; _currentEvent = null
        clearEventOutcomeMessage()

        _character.update { it?.recoverEnergy() }

        // Сначала переходим в следующий год
        _currentYear.update { it + 1 }
        val newYear = _currentYear.value

        _character.update { currentCharacter ->
            currentCharacter?.let { char ->
                val aged = char.copy(
                    age = char.age + 1,
                    health = (char.health - 1).coerceIn(0, 100),
                    happiness = (char.happiness - 1).coerceIn(0, 100),
                    money = (char.money + char.moneyPerYear).coerceAtLeast(0)
                )

                // Проверка окончания жизни
                if (aged.age >= 90 || aged.health <= 0) {
                    _isGameOver.value = true
                    _eventOutcomeMessage.value =
                        if (aged.age >= 90)
                            "${char.name} прожил долгую жизнь и мирно скончался в ${aged.age} лет."
                        else
                            "${char.name} скончался в ${aged.age} лет от проблем со здоровьем."
                    return@update aged
                }

                return@update findAndApplyEvent(aged, newYear)
            }
        }
    }

    fun study() {
                _character.update { char ->
                        char?.let {
                                // Условия: достаточно энергии и учимся только в школе/универе
                                if (it.energy >= 20 && it.educationLevel in EducationLevel.PRIMARY_SCHOOL..EducationLevel.BACHELOR) {
                                        val spentEnergy = 20
                                        val iqGain      = 5
                                        val happyBonus  = 2

                                        it.copy(
                                                energy       = it.energy - spentEnergy,
                                               intelligence = (it.intelligence + iqGain).coerceAtMost(100),
                                                happiness    = (it.happiness + happyBonus).coerceAtMost(100)
                                                    )
                                    } else it
                            }
                    }
                // После учёбы можно вернуться на главный экран без диалога
          }

    private fun findAndApplyEvent(charBefore: Character, year: Int): Character {
        val evt = GameEvents.events
            .firstOrNull { it.isMandatory && it.condition(charBefore, year) }
            ?: GameEvents.events.filter { !it.isMandatory && it.condition(charBefore, year) }
                .randomOrNull()

        if (evt == null) {
            // ЗДЕСЬ УБИРАЕМ SHOW_DIALOG и MESSAGE
            return charBefore
        }

        _currentEvent.value    = evt
        _showEventDialog.value = true

        // если у события есть directOutcome — сразу применяем его
        evt.directOutcome?.let { out ->
            _eventOutcomeMessage.value = out.description
            val applied = charBefore.applyOutcomeStats(out)
            return out.customAction?.invoke(applied) ?: applied
        }

        // иначе — событие с выбором; ждем applyOutcome()
        return charBefore
    }



    fun applyOutcome(outcome: GameEventOutcome) {
        _character.update { char ->
            char?.let {
                val afterStats = it.applyOutcomeStats(outcome)
                _eventOutcomeMessage.value = outcome.description
                Log.d("GVM", "Исход: ${outcome.description}")
                outcome.customAction?.invoke(afterStats) ?: afterStats
            }
        }
    }

    // Вынесем логику применения изменений в одно место
    private fun Character.applyOutcomeStats(outcome: GameEventOutcome): Character {
        var updated = copy(
            health = (health + outcome.healthChange).coerceIn(0, 100),
            happiness = (happiness + outcome.happinessChange).coerceIn(0, 100),
            money = (money + outcome.moneyChange).coerceAtLeast(0)
        )
        if (outcome.healthChangePercent != 0.0)
            updated = updated.copy(health = (updated.health * (1 + outcome.healthChangePercent)).toInt().coerceIn(0, 100))
        if (outcome.happinessChangePercent != 0.0)
            updated = updated.copy(happiness = (updated.happiness * (1 + outcome.happinessChangePercent)).toInt().coerceIn(0, 100))
        if (outcome.moneyChangePercent != 0.0)
            updated = updated.copy(money = (updated.money * (1 + outcome.moneyChangePercent)).toInt().coerceAtLeast(0))
        outcome.setEducationLevel?.let { updated = updated.copy(educationLevel = it) }
        outcome.setHasChronicDisease?.let { updated = updated.copy(hasChronicDisease = it) }
        return updated
    }

    fun clearEventOutcomeMessage() {
        _eventOutcomeMessage.value = null
    }

    fun hideEventDialog() {
        _showEventDialog.value = false
        _currentEvent.value = null
    }

    fun resetGame() {
        _character.value = null
        _currentYear.value = 1960
        _eventOutcomeMessage.value = null
        _showEventDialog.value = false
        _currentEvent.value = null
        _isGameOver.value = false
        Log.d("GameViewModel", "Игра сброшена.")
    }
}
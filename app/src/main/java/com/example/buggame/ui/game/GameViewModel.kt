package com.example.buggame.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buggame.data.model.GameSettings
import com.example.buggame.data.model.GameUiState
import com.example.buggame.data.model.InsectSpawn
import com.example.buggame.data.model.InsectType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var spawnJob: Job? = null
    private var nextInsectId = 0L

    private var settings: GameSettings = GameSettings()

    private companion object {
        const val MISS_PENALTY = 3
    }

    fun updateSettings(newSettings: GameSettings) {
        settings = newSettings
    }

    fun onStartClicked() {
        val current = _uiState.value
        if (current.isRunning) return

        _uiState.value = if (current.isRoundOver || current.timeLeftSeconds <= 0) {
            GameUiState(
                isRunning = true,
                timeLeftSeconds = settings.roundDurationSeconds
            )
        } else {
            current.copy(isRunning = true)
        }
        startTimer()
        startSpawning()
    }

    fun onPauseClicked() {
        timerJob?.cancel()
        spawnJob?.cancel()
        _uiState.value = _uiState.value.copy(isRunning = false)
    }

    fun onResetClicked() {
        timerJob?.cancel()
        spawnJob?.cancel()
        _uiState.value = GameUiState(timeLeftSeconds = settings.roundDurationSeconds)
    }

    fun onRoundOverDialogDismissed() {
        _uiState.value = _uiState.value.copy(isRoundOver = false)
    }

    fun onInsectTapped(insectId: Long) {
        val current = _uiState.value
        if (!current.isRunning) return
        val insect = current.insects.find { it.id == insectId } ?: return

        _uiState.value = current.copy(
            insects = current.insects.filterNot { it.id == insectId },
            score = (current.score + insect.type.points).coerceAtLeast(0)
        )
    }

    fun onMissTapped() {
        val current = _uiState.value
        if (!current.isRunning) return

        _uiState.value = current.copy(
            score = (current.score - MISS_PENALTY).coerceAtLeast(0)
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isRunning && _uiState.value.timeLeftSeconds > 0) {
                delay(1000)
                val current = _uiState.value
                if (!current.isRunning) break

                val newTime = (current.timeLeftSeconds - 1).coerceAtLeast(0)
                val roundOver = newTime <= 0
                _uiState.value = current.copy(
                    timeLeftSeconds = newTime,
                    isRunning = !roundOver,
                    isRoundOver = roundOver,
                    insects = if (roundOver) emptyList() else current.insects
                )
            }
        }
    }

    private fun startSpawning() {
        spawnJob?.cancel()
        spawnJob = viewModelScope.launch {
            while (_uiState.value.isRunning) {
                delay(settings.insectSpawnIntervalMillis)

                val current = _uiState.value
                if (current.isRunning && current.insects.size < settings.maxBugsOnScreen) {
                    val newInsect = InsectSpawn(
                        id = ++nextInsectId,
                        type = InsectType.entries.random()
                    )
                    _uiState.value = current.copy(insects = current.insects + newInsect)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        spawnJob?.cancel()
    }
}
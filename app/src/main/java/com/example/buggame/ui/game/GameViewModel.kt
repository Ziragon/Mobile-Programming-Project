package com.example.buggame.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buggame.data.model.GameUiState
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

    fun onStartClicked() {
        val current = _uiState.value
        if (current.isRunning) return

        _uiState.value = if (current.isRoundOver || current.timeLeftSeconds <= 0) {
            GameUiState(isRunning = true)
        } else {
            current.copy(isRunning = true)
        }
        startTimer()
    }

    fun onPauseClicked() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isRunning = false)
    }

    fun onResetClicked() {
        timerJob?.cancel()
        _uiState.value = GameUiState()
    }

    fun onRoundOverDialogDismissed() {
        _uiState.value = _uiState.value.copy(isRoundOver = false)
    }

    fun addScore(points: Int) {
        _uiState.value = _uiState.value.copy(score = (_uiState.value.score + points).coerceAtLeast(0))
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isRunning && _uiState.value.timeLeftSeconds > 0) {
                delay(1000)
                val current = _uiState.value
                if (!current.isRunning) break

                val newTime = (current.timeLeftSeconds - 1).coerceAtLeast(0)
                _uiState.value = current.copy(
                    timeLeftSeconds = newTime,
                    isRunning = newTime > 0,
                    isRoundOver = newTime <= 0
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
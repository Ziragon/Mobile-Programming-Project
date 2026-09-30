package com.example.buggame.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    fun onGameSpeedChanged(speed: Float) {
        _uiState.update {
            it.copy(settings = it.settings.copy(gameSpeed = speed))
        }
    }

    fun onMaxBugsChanged(count: Int) {
        _uiState.update {
            it.copy(settings = it.settings.copy(maxBugsOnScreen = count))
        }
    }

    fun onBonusIntervalChanged(seconds: Int) {
        _uiState.update {
            it.copy(settings = it.settings.copy(bonusIntervalSeconds = seconds))
        }
    }

    fun onRoundDurationChanged(seconds: Int) {
        _uiState.update {
            it.copy(settings = it.settings.copy(roundDurationSeconds = seconds))
        }
    }
}
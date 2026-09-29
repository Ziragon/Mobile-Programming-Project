package com.example.buggame.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.buggame.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    init {
        val loaded = repository.loadSettings()
        _uiState.update { it.copy(settings = loaded) }
    }

    fun onGameSpeedChanged(speed: Float) {
        _uiState.update {
            it.copy(
                settings = it.settings.copy(gameSpeed = speed),
                isSavedSuccess = false
            )
        }
    }

    fun onMaxBugsChanged(count: Int) {
        _uiState.update {
            it.copy(
                settings = it.settings.copy(maxBugsOnScreen = count),
                isSavedSuccess = false
            )
        }
    }

    fun onBonusIntervalChanged(seconds: Int) {
        _uiState.update {
            it.copy(
                settings = it.settings.copy(bonusIntervalSeconds = seconds),
                isSavedSuccess = false
            )
        }
    }

    fun onRoundDurationChanged(seconds: Int) {
        _uiState.update {
            it.copy(
                settings = it.settings.copy(roundDurationSeconds = seconds),
                isSavedSuccess = false
            )
        }
    }

    fun onSaveClicked() {
        val currentSettings = _uiState.value.settings
        repository.saveSettings(currentSettings)
        _uiState.update { it.copy(isSavedSuccess = true) }
    }

    fun onSaveSnackbarDismissed() {
        _uiState.update { it.copy(isSavedSuccess = false) }
    }
}

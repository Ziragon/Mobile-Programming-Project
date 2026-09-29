package com.example.buggame.ui.rules

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.buggame.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RulesViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(RulesState(isLoading = true))
    val uiState: StateFlow<RulesState> = _uiState.asStateFlow()

    init {
        loadRules()
    }

    fun loadRules(playerName: String = "Игрок", difficulty: String = "Стандартная") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val formattedHtml = withContext(Dispatchers.IO) {
                    val rawHtml = getApplication<Application>()
                        .resources
                        .openRawResource(R.raw.game_rules)
                        .bufferedReader()
                        .use { it.readText() }

                    String.format(rawHtml, playerName, difficulty)
                }

                _uiState.update {
                    it.copy(
                        htmlContent = formattedHtml,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Ошибка при загрузке правил: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
}

package com.example.buggame.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buggame.data.repository.ScoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LeaderboardViewModel(
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(LeaderboardFilterTab.BY_SCORE)
    private val _rawScores = scoreRepository.allScores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<LeaderboardState> = combine(_selectedTab, _rawScores) { tab, scores ->
        val filtered = when (tab) {
            LeaderboardFilterTab.BY_SCORE -> scores.sortedByDescending { it.score }
            LeaderboardFilterTab.BY_RATIO -> scores.sortedByDescending { it.scorePerSecond }
            LeaderboardFilterTab.HEAVY_MODE -> scores
                .filter { it.difficultyName == "Тяжелый" }
                .sortedByDescending { it.score }
        }
        LeaderboardState(
            selectedTab = tab,
            scores = filtered,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LeaderboardState())

    fun onTabSelected(tab: LeaderboardFilterTab) {
        _selectedTab.value = tab
    }

    fun onClearScoresClicked() {
        viewModelScope.launch {
            scoreRepository.clearScores()
        }
    }
}

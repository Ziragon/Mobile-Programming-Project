package com.example.buggame.ui.leaderboard

import com.example.buggame.data.model.ScoreRecord

enum class LeaderboardFilterTab(val title: String) {
    BY_SCORE("По очкам"),
    BY_RATIO("Очки / Время"),
    HEAVY_MODE("Тяжелый")
}

data class LeaderboardState(
    val selectedTab: LeaderboardFilterTab = LeaderboardFilterTab.BY_SCORE,
    val scores: List<ScoreRecord> = emptyList(),
    val isLoading: Boolean = false
)

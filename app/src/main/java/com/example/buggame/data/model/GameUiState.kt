package com.example.buggame.data.model

data class GameUiState(
    val score: Int = 0,
    val timeLeftSeconds: Int = DEFAULT_ROUND_DURATION_SECONDS,
    val isRunning: Boolean = false,
    val isRoundOver: Boolean = false,
    val isMidRound: Boolean = false,
    val insects: List<InsectSpawn> = emptyList()
) {
    companion object {
        const val DEFAULT_ROUND_DURATION_SECONDS = 60
    }
}
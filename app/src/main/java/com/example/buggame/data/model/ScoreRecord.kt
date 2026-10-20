package com.example.buggame.data.model

data class ScoreRecord(
    val id: Long = 0,
    val playerName: String,
    val score: Int,
    val difficultyLevel: Int,
    val difficultyName: String,
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    val scorePerTenSecond: Float
        get() = if (durationSeconds > 0) score.toFloat() * 10 / durationSeconds else 0f
}

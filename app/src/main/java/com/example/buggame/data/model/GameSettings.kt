package com.example.buggame.data.model

data class GameSettings(
    val gameSpeed: Float = 1.0f,
    val maxBugsOnScreen: Int = 10,
    val bonusIntervalSeconds: Int = 15,
    val roundDurationSeconds: Int = 60
)

package com.example.buggame.data.model

fun getDifficultyName(speed: Float): String = when {
    speed <= 1.2f -> "Очень легкий"
    speed <= 1.7f -> "Легкий"
    speed <= 2.2f -> "Нормальный"
    speed <= 2.7f -> "Сложный"
    else -> "Тяжелый"
}

data class GameSettings(
    val gameSpeed: Float = 2.0f,
    val maxBugsOnScreen: Int = 10,
    val bonusIntervalSeconds: Float = 7.5f,
    val roundDurationSeconds: Int = 30
) {
    val difficultyName: String
        get() = getDifficultyName(gameSpeed)
}

package com.example.buggame.data.model

fun getDifficultyName(speed: Float): String = when (speed.toInt()) {
    1 -> "Очень легкий"
    2 -> "Легкий"
    3 -> "Нормальный"
    4 -> "Сложный"
    5 -> "Тяжелый"
    else -> "Нормальный"
}

data class GameSettings(
    val gameSpeed: Float = 3f,
    val maxBugsOnScreen: Int = 10,
    val bonusIntervalSeconds: Float = 7.5f,
    val roundDurationSeconds: Int = 30
) {
    val difficultyName: String
        get() = getDifficultyName(gameSpeed)
}

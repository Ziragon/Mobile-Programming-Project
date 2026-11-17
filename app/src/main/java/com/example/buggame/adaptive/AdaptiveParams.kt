package com.example.buggame.adaptive

import kotlin.math.roundToInt

data class AdaptiveParams(
    val difficulty: Double,
    val level: Int,
    val speedMultiplier: Float,
    val spawnIntervalMultiplier: Float,
    val maxBugsBonus: Int,
    val missPenalty: Int
) {
    fun adjustSpawnInterval(baseMillis: Long): Long =
        (baseMillis * spawnIntervalMultiplier).toLong()
            .coerceAtLeast(DdaConfig.MIN_SPAWN_INTERVAL_MS)

    fun adjustMaxBugs(base: Int): Int = (base + maxBugsBonus).coerceAtLeast(1)

    companion object {
        fun forDifficulty(value: Double): AdaptiveParams {
            val d = value.coerceIn(-1.0, 1.0)
            return AdaptiveParams(
                difficulty = d,
                level = (d * 3).roundToInt(),
                speedMultiplier = (1.0 + 0.4 * d).toFloat(),
                spawnIntervalMultiplier = (1.0 - 0.35 * d).toFloat(),
                maxBugsBonus = (3 * d).roundToInt(),
                missPenalty = (3 + 2 * d).roundToInt().coerceIn(1, 5)
            )
        }
    }
}
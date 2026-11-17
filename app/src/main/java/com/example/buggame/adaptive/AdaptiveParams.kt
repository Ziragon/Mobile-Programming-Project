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
            val up = d.coerceAtLeast(0.0)
            val down = d.coerceAtMost(0.0)

            return AdaptiveParams(
                difficulty = d,
                level = (d * 3).roundToInt(),
                speedMultiplier = (1.0 + 1.0 * up + 0.5 * down).toFloat(),
                spawnIntervalMultiplier = (1.0 - 0.8 * up - 0.5 * down).toFloat(),
                maxBugsBonus = (7 * up + 3 * down).roundToInt(),
                missPenalty = (3 + 4 * up + 2 * down).roundToInt().coerceIn(1, 7)
            )
        }
    }
}
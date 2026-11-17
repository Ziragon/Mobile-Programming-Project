package com.example.buggame.adaptive

data class RoundStats(val hits: Int, val misses: Int) {
    val attempts: Int get() = hits + misses
    val accuracy: Double get() = if (attempts == 0) 0.0 else hits.toDouble() / attempts
}

data class AdaptiveParams(
    val level: Int,
    val speedMultiplier: Float,
    val spawnIntervalMultiplier: Float,
    val maxBugsBonus: Int,
    val missPenalty: Int
) {
    fun adjustSpawnInterval(baseMillis: Long): Long =
        (baseMillis * spawnIntervalMultiplier).toLong()
            .coerceAtLeast(AdaptiveDifficulty.MIN_SPAWN_INTERVAL_MS)

    fun adjustMaxBugs(base: Int): Int = (base + maxBugsBonus).coerceAtLeast(1)
}

object AdaptiveDifficulty {
    const val MIN_LEVEL = -2
    const val MAX_LEVEL = 3
    const val MIN_ATTEMPTS = 5
    const val RAISE_ACCURACY = 0.85
    const val LOWER_ACCURACY = 0.50
    const val BASE_MISS_PENALTY = 3
    const val MIN_SPAWN_INTERVAL_MS = 200L

    fun nextLevel(current: Int, stats: RoundStats): Int {
        if (stats.attempts < MIN_ATTEMPTS) return current.coerceIn(MIN_LEVEL, MAX_LEVEL)
        val delta = when {
            stats.accuracy >= RAISE_ACCURACY -> 1
            stats.accuracy < LOWER_ACCURACY -> -1
            else -> 0
        }
        return (current + delta).coerceIn(MIN_LEVEL, MAX_LEVEL)
    }

    fun paramsFor(level: Int): AdaptiveParams {
        val l = level.coerceIn(MIN_LEVEL, MAX_LEVEL)
        return AdaptiveParams(
            level = l,
            speedMultiplier = 1f + 0.15f * l,
            spawnIntervalMultiplier = 1f - 0.10f * l,
            maxBugsBonus = l,
            missPenalty = (BASE_MISS_PENALTY + l).coerceIn(1, 6)
        )
    }
}
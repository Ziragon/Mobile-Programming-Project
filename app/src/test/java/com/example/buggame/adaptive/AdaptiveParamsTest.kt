package com.example.buggame.adaptive

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveParamsTest {

    @Test
    fun neutralDifficulty_producesDefaultParameters() {
        val params = AdaptiveParams.forDifficulty(0.0)

        assertEquals(0.0, params.difficulty, 0.0001)
        assertEquals(0, params.level)
        assertEquals(1.0f, params.speedMultiplier, 0.0001f)
        assertEquals(1.0f, params.spawnIntervalMultiplier, 0.0001f)
        assertEquals(0, params.maxBugsBonus)
        assertEquals(3, params.missPenalty)
    }

    @Test
    fun extremeBoundaries_clampCorrectlyAndCalculateExtremes() {
        val maxParams = AdaptiveParams.forDifficulty(1.5)
        assertEquals(1.0, maxParams.difficulty, 0.0001)
        assertEquals(3, maxParams.level)
        assertEquals(2.0f, maxParams.speedMultiplier, 0.0001f)
        assertEquals(0.2f, maxParams.spawnIntervalMultiplier, 0.0001f)
        assertEquals(7, maxParams.maxBugsBonus)
        assertEquals(7, maxParams.missPenalty)

        // Проверка минимума и ограничения при выходе за нижнюю границу (< -1.0)
        val minParams = AdaptiveParams.forDifficulty(-2.0)
        assertEquals(-1.0, minParams.difficulty, 0.0001)
        assertEquals(-3, minParams.level)
        assertEquals(0.5f, minParams.speedMultiplier, 0.0001f)
        assertEquals(1.5f, minParams.spawnIntervalMultiplier, 0.0001f)
        assertEquals(-3, minParams.maxBugsBonus)
        assertEquals(1, minParams.missPenalty)
    }

    @Test
    fun adjustMethods_respectSafetyFloorLimits() {
        val maxParams = AdaptiveParams.forDifficulty(1.0)
        val clampedSpawnInterval = maxParams.adjustSpawnInterval(baseMillis = 500L)
        assertEquals(DdaConfig.MIN_SPAWN_INTERVAL_MS, clampedSpawnInterval)

        val minParams = AdaptiveParams.forDifficulty(-1.0)
        val clampedMaxBugs = minParams.adjustMaxBugs(base = 2)
        assertEquals(1, clampedMaxBugs)
        assertTrue(clampedMaxBugs >= 1)
    }
}

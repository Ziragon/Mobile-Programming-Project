package com.example.buggame.adaptive

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AdaptiveControllerTest {

    private lateinit var controller: AdaptiveController

    @Before
    fun setUp() {
        controller = AdaptiveController(startDifficulty = 0.0)
    }

    @Test
    fun coldStart_doesNotChangeDifficultyUntilMinEventsReached() {
        repeat(DdaConfig.MIN_EVENTS - 1) {
            controller.onHit(reactionMs = 300L)
        }

        val initialDifficulty = controller.difficulty
        controller.tick(bugsOnScreen = 0, maxBugs = 5)

        assertEquals(
            "Сложность не должна адаптироваться до накопления минимального количества событий",
            initialDifficulty,
            controller.difficulty,
            0.0001
        )
    }

    @Test
    fun highPerformance_graduallyIncreasesDifficulty_boundedByMaxStep() {
        repeat(DdaConfig.MIN_EVENTS) {
            controller.onHit(reactionMs = 300L)
        }

        val diffBeforeTick = controller.difficulty
        controller.tick(bugsOnScreen = 0, maxBugs = 5)

        assertTrue(
            "Скилл игрока должен превышать целевой порог при идеальной игре",
            controller.lastSkill > DdaConfig.TARGET_SKILL + DdaConfig.DEAD_ZONE
        )

        val delta = controller.difficulty - diffBeforeTick
        assertTrue("Сложность должна вырасти", delta > 0)
        assertTrue(
            "Шаг адаптации за один тик не должен превышать DdaConfig.MAX_STEP",
            delta <= DdaConfig.MAX_STEP + 0.0001
        )

        repeat(30) {
            controller.onHit(reactionMs = 300L)
            controller.tick(bugsOnScreen = 0, maxBugs = 5)
        }
        assertEquals(1.0, controller.difficulty, 0.0001)
    }

    @Test
    fun missStreak_triggersImmediateDifficultyReduction() {
        val startDiff = 0.5
        val testController = AdaptiveController(startDifficulty = startDiff)

        testController.onMiss()
        testController.onMiss()
        assertEquals(
            "Сложность не должна падать мгновенно до достижения лимита серии промахов",
            startDiff,
            testController.difficulty,
            0.0001
        )

        testController.onMiss()
        assertEquals(
            "Сложность должна снизиться на MISS_STREAK_EASE при серии промахов",
            startDiff - DdaConfig.MISS_STREAK_EASE,
            testController.difficulty,
            0.0001
        )

        testController.onHit()
        testController.onMiss()
        testController.onMiss()
        assertEquals(
            "Попадание должно сбрасывать серию промахов",
            startDiff - DdaConfig.MISS_STREAK_EASE,
            testController.difficulty,
            0.0001
        )
    }

    @Test
    fun finishRoundAndReset_behaveCorrectly() {
        val testController = AdaptiveController(startDifficulty = 0.8)

        val nextRoundParams = testController.finishRound()
        assertEquals(
            "Сложность после раунда должна масштабироваться на ROUND_CARRY",
            0.8 * DdaConfig.ROUND_CARRY,
            testController.difficulty,
            0.0001
        )
        assertEquals(testController.difficulty, nextRoundParams.difficulty, 0.0001)

        testController.tick(bugsOnScreen = 0, maxBugs = 5)
        assertEquals(0.8 * DdaConfig.ROUND_CARRY, testController.difficulty, 0.0001)

        testController.reset()
        assertEquals(0.0, testController.difficulty, 0.0001)
        assertEquals(DdaConfig.TARGET_SKILL, testController.lastSkill, 0.0001)
    }
}

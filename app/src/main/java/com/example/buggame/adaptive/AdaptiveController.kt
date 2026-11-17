package com.example.buggame.adaptive

import kotlin.math.abs

class AdaptiveController(startDifficulty: Double = 0.0) {

    var difficulty: Double = startDifficulty.coerceIn(-1.0, 1.0)
        private set

    var lastSkill: Double = DdaConfig.TARGET_SKILL
        private set

    private var accuracy = DdaConfig.INITIAL_ACCURACY
    private var reaction = DdaConfig.INITIAL_REACTION
    private var events = 0
    private var missStreak = 0

    val params: AdaptiveParams get() = AdaptiveParams.forDifficulty(difficulty)

    fun onHit(reactionMs: Long? = null) {
        accuracy = ema(accuracy, 1.0, DdaConfig.ACC_ALPHA)
        if (reactionMs != null) {
            reaction = ema(reaction, reactionScore(reactionMs), DdaConfig.RT_ALPHA)
        }
        events++
        missStreak = 0
    }

    fun onMiss() {
        accuracy = ema(accuracy, 0.0, DdaConfig.ACC_ALPHA)
        events++
        missStreak++
        if (missStreak >= DdaConfig.MISS_STREAK_LIMIT) {
            difficulty = (difficulty - DdaConfig.MISS_STREAK_EASE).coerceIn(-1.0, 1.0)
            missStreak = 0
        }
    }

    fun tick(bugsOnScreen: Int, maxBugs: Int): AdaptiveParams {
        val pressure = if (maxBugs <= 0) 0.0
        else (bugsOnScreen.toDouble() / maxBugs).coerceIn(0.0, 1.0)

        val skill = DdaConfig.W_ACCURACY * accuracy +
                DdaConfig.W_REACTION * reaction +
                DdaConfig.W_PRESSURE * (1.0 - pressure)
        lastSkill = skill

        if (events >= DdaConfig.MIN_EVENTS) {
            val error = skill - DdaConfig.TARGET_SKILL
            if (abs(error) > DdaConfig.DEAD_ZONE) {
                val step = (DdaConfig.GAIN * error)
                    .coerceIn(-DdaConfig.MAX_STEP, DdaConfig.MAX_STEP)
                difficulty = (difficulty + step).coerceIn(-1.0, 1.0)
            }
        }
        return params
    }

    fun startRound() {
        accuracy = DdaConfig.INITIAL_ACCURACY
        reaction = DdaConfig.INITIAL_REACTION
        events = 0
        missStreak = 0
    }

    fun finishRound(): AdaptiveParams {
        difficulty *= DdaConfig.ROUND_CARRY
        startRound()
        return params
    }

    fun reset() {
        difficulty = 0.0
        lastSkill = DdaConfig.TARGET_SKILL
        startRound()
    }

    private fun ema(old: Double, sample: Double, alpha: Double) = old + alpha * (sample - old)

    private fun reactionScore(ms: Long): Double {
        val span = DdaConfig.SLOW_RT_MS - DdaConfig.FAST_RT_MS
        return 1.0 - ((ms - DdaConfig.FAST_RT_MS) / span).coerceIn(0.0, 1.0)
    }
}
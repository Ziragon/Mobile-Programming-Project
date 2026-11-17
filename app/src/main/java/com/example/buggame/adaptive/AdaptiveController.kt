package com.example.buggame.adaptive

class AdaptiveController(startLevel: Int = 0) {

    var level: Int = startLevel.coerceIn(AdaptiveDifficulty.MIN_LEVEL, AdaptiveDifficulty.MAX_LEVEL)
        private set

    private var hits = 0
    private var misses = 0

    val params: AdaptiveParams get() = AdaptiveDifficulty.paramsFor(level)

    fun onHit() { hits++ }
    fun onMiss() { misses++ }

    fun startRound() { hits = 0; misses = 0 }

    fun finishRound(): AdaptiveParams {
        level = AdaptiveDifficulty.nextLevel(level, RoundStats(hits, misses))
        startRound()
        return params
    }

    fun reset() {
        level = 0
        startRound()
    }
}
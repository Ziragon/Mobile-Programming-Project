package com.example.buggame.adaptive

object DdaConfig {
    const val TICK_MS = 2000L

    const val TARGET_SKILL = 0.65
    const val DEAD_ZONE = 0.05
    const val GAIN = 0.5
    const val MAX_STEP = 0.06
    const val MIN_EVENTS = 6

    const val ACC_ALPHA = 0.15
    const val RT_ALPHA = 0.25
    const val INITIAL_ACCURACY = 0.7
    const val INITIAL_REACTION = 0.5

    const val FAST_RT_MS = 600.0
    const val SLOW_RT_MS = 3000.0

    const val W_ACCURACY = 0.45
    const val W_REACTION = 0.35
    const val W_PRESSURE = 0.20

    const val MISS_STREAK_LIMIT = 3
    const val MISS_STREAK_EASE = 0.1
    const val ROUND_CARRY = 0.5

    const val MIN_SPAWN_INTERVAL_MS = 200L
}
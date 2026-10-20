package com.example.buggame.ui.game

import com.example.buggame.data.model.GameSettings

private const val BASE_INSECT_SPEED_PX_PER_SEC = 200f
private const val BASE_SPAWN_INTERVAL_MILLIS = 1600L

val GameSettings.insectSpeedPxPerSec: Float
    get() = BASE_INSECT_SPEED_PX_PER_SEC * gameSpeed

val GameSettings.insectSpawnIntervalMillis: Long
    get() = (BASE_SPAWN_INTERVAL_MILLIS / gameSpeed)
        .toLong()
        .coerceIn(200L, 3000L)
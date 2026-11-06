package com.example.buggame.data.model

import kotlin.math.roundToInt


data class GoldRate(
    val pricePerGram: Double,
    val dateMillis: Long
) {
    val bugPoints: Int get() = (pricePerGram / 100.0).roundToInt().coerceAtLeast(1)
}
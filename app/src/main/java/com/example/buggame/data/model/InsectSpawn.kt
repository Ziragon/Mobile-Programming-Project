package com.example.buggame.data.model


import androidx.annotation.DrawableRes

data class InsectSpawn(
    val id: Long,
    val type: InsectType,
    val isBonus: Boolean = false
) {
    @get:DrawableRes
    val iconRes: Int get() = if (isBonus) BonusInsect.iconRes else type.iconRes

    val points: Int get() = if (isBonus) BonusInsect.points else type.points

    val contentDescription: String
        get() = if (isBonus) BonusInsect.contentDescription else type.contentDescription

    val speedMultiplier: Float get() = if (isBonus) BonusInsect.speedMultiplier else 1f
}
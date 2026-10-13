package com.example.buggame.data.model

import androidx.annotation.DrawableRes
import com.example.buggame.R

object BonusInsect {
    @DrawableRes val iconRes: Int = R.drawable.insect_bonus
    const val points: Int = 25
    const val contentDescription: String = "Бонусное насекомое"
    const val speedMultiplier: Float = 1.5f
}
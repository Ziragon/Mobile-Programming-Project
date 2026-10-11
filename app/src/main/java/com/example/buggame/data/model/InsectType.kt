package com.example.buggame.data.model

import androidx.annotation.DrawableRes
import com.example.buggame.R

enum class InsectType(
    @DrawableRes val iconRes: Int,
    val points: Int,
    val contentDescription: String
) {
    COCKROACH(R.drawable.insect_cockroach, points = 5, contentDescription = "Таракан"),
    FLY(R.drawable.insect_fly, points = 10, contentDescription = "Муха"),
    SPIDER(R.drawable.insect_spider, points = 8, contentDescription = "Паук"),
    ANT(R.drawable.insect_ant, points = 6, contentDescription = "Муравей"),
    BEETLE(R.drawable.insect_beetle, points = 12, contentDescription = "Жук")
}
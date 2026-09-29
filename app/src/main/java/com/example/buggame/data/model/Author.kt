package com.example.buggame.data.model

import androidx.annotation.DrawableRes

data class Author(
    val id: Long,
    val fullName: String,
    val role: String = "Разработчик",
    @DrawableRes val photoResId: Int? = null
)

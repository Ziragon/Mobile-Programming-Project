package com.example.buggame.data.model

import java.time.LocalDate

enum class Gender(val title: String) {
    MALE("Мужской"),
    FEMALE("Женский")
}

data class PlayerProfile(
    val fullName: String,
    val gender: Gender,
    val course: Int,
    val difficulty: Int,
    val birthDate: LocalDate,
    val zodiacSign: ZodiacSign
)

enum class ZodiacSign(val title: String) {
    ARIES("Овен"),
    TAURUS("Телец"),
    GEMINI("Близнецы"),
    CANCER("Рак"),
    LEO("Лев"),
    VIRGO("Дева"),
    LIBRA("Весы"),
    SCORPIO("Скорпион"),
    SAGITTARIUS("Стрелец"),
    CAPRICORN("Козерог"),
    AQUARIUS("Водолей"),
    PISCES("Рыбы")
}
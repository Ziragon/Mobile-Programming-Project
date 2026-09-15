package com.example.buggame.data.model

import java.time.LocalDate

enum class Gender(val title: String) {
    MALE("Мужской"),
    FEMALE("Женский")
}

enum class Course(val number: Int, val title: String) {
    FIRST(1, "1 Курс"),
    SECOND(2, "2 Курс"),
    THIRD(3, "3 Курс"),
    FOURTH(4, "4 Курс");
}

data class PlayerProfile(
    val fullName: String,
    val gender: Gender,
    val course: Course,
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
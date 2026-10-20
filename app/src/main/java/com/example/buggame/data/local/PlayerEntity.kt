package com.example.buggame.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.buggame.data.model.Course
import com.example.buggame.data.model.Gender
import com.example.buggame.data.model.PlayerProfile
import com.example.buggame.data.model.ZodiacSign
import java.time.LocalDate

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val genderName: String,
    val courseName: String,
    val birthDateEpochDay: Long,
    val zodiacSignName: String
)

fun PlayerEntity.toDomain(): PlayerProfile = PlayerProfile(
    id = id,
    fullName = fullName,
    gender = Gender.valueOf(genderName),
    course = Course.valueOf(courseName),
    birthDate = LocalDate.ofEpochDay(birthDateEpochDay),
    zodiacSign = ZodiacSign.valueOf(zodiacSignName)
)

fun PlayerProfile.toEntity(): PlayerEntity = PlayerEntity(
    id = id,
    fullName = fullName,
    genderName = gender.name,
    courseName = course.name,
    birthDateEpochDay = birthDate.toEpochDay(),
    zodiacSignName = zodiacSign.name
)

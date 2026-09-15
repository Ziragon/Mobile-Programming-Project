package com.example.buggame.ui.registration

import com.example.buggame.data.model.Course
import com.example.buggame.data.model.Gender
import com.example.buggame.data.model.PlayerProfile
import java.time.LocalDate

data class RegistrationState(
    val fullName: String = "",
    val gender: Gender = Gender.MALE,
    val course: Course = Course.FIRST,
    val difficulty: Int = 1,
    val birthDate: LocalDate = LocalDate.now(),

    val submittedProfile: PlayerProfile? = null,
    val summaryText: String = ""
)

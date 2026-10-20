package com.example.buggame.ui.registration

import com.example.buggame.data.model.Course
import com.example.buggame.data.model.Gender
import com.example.buggame.data.model.PlayerProfile
import java.time.LocalDate

enum class RegistrationMode {
    CREATE_NEW,
    SELECT_EXISTING
}

data class RegistrationState(
    val mode: RegistrationMode = RegistrationMode.CREATE_NEW,
    val savedPlayers: List<PlayerProfile> = emptyList(),
    val activePlayer: PlayerProfile? = null,

    val fullName: String = "",
    val gender: Gender = Gender.MALE,
    val course: Course = Course.FIRST,
    val birthDate: LocalDate = LocalDate.now(),

    val submittedProfile: PlayerProfile? = null,
    val summaryText: String = "",
    val isResultDialogVisible: Boolean = false
)

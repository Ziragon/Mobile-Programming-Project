package com.example.buggame.ui.registration

import androidx.lifecycle.ViewModel
import com.example.buggame.data.model.Course
import com.example.buggame.data.model.Gender
import com.example.buggame.data.model.PlayerProfile
import com.example.buggame.domain.ZodiacCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class RegistrationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationState())
    val uiState: StateFlow<RegistrationState> = _uiState.asStateFlow()

    fun onFullNameChanged(name: String) {
        _uiState.update { it.copy(fullName = name) }
    }

    fun onGenderChanged(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onCourseChanged(course: Course) {
        _uiState.update { it.copy(course = course) }
    }

    fun onDifficultyChanged(difficulty: Int) {
        _uiState.update { it.copy(difficulty = difficulty) }
    }

    fun onBirthDateChanged(date: LocalDate) {
        _uiState.update { it.copy(birthDate = date) }
    }

    fun onResultDialogDismissed() {
        _uiState.update { it.copy(isResultDialogVisible = false) }
    }

    fun onSubmitClicked() {
        val currentState = _uiState.value
        val zodiac = ZodiacCalculator.getZodiacSign(currentState.birthDate)

        val profile = PlayerProfile(
            fullName = currentState.fullName,
            gender = currentState.gender,
            course = currentState.course,
            difficulty = currentState.difficulty,
            birthDate = currentState.birthDate,
            zodiacSign = zodiac
        )

        val formattedSummary = """
            Игрок: ${profile.fullName}
            Пол: ${profile.gender.title}
            Курс: ${profile.course.title}
            Сложность: ${profile.difficulty}
            Дата рождения: ${profile.birthDate}
            Знак зодиака: ${profile.zodiacSign.title}
        """.trimIndent()

        _uiState.update {
            it.copy(
                submittedProfile = profile,
                summaryText = formattedSummary,
                isResultDialogVisible = true
            )
        }
    }
}

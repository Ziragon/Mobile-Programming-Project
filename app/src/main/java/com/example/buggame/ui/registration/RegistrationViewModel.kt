package com.example.buggame.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buggame.data.model.Course
import com.example.buggame.data.model.Gender
import com.example.buggame.data.model.PlayerProfile
import com.example.buggame.data.repository.PlayerRepository
import com.example.buggame.domain.ZodiacCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class RegistrationViewModel(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationState())
    val uiState: StateFlow<RegistrationState> = _uiState.asStateFlow()

    init {
        playerRepository.allPlayers
            .onEach { players ->
                _uiState.update { it.copy(savedPlayers = players) }
            }
            .launchIn(viewModelScope)

        playerRepository.currentPlayer
            .onEach { current ->
                _uiState.update { it.copy(activePlayer = current) }
            }
            .launchIn(viewModelScope)
    }

    fun onModeChanged(mode: RegistrationMode) {
        _uiState.update { it.copy(mode = mode) }
    }

    fun onFullNameChanged(name: String) {
        _uiState.update { it.copy(fullName = name) }
    }

    fun onGenderChanged(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onCourseChanged(course: Course) {
        _uiState.update { it.copy(course = course) }
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

        val newProfile = PlayerProfile(
            fullName = currentState.fullName.trim(),
            gender = currentState.gender,
            course = currentState.course,
            birthDate = currentState.birthDate,
            zodiacSign = zodiac
        )

        viewModelScope.launch {
            val savedProfile = playerRepository.registerAndSelectPlayer(newProfile)
            showProfileSummaryDialog(savedProfile)
        }
    }

    fun onExistingPlayerSelected(profile: PlayerProfile) {
        playerRepository.selectPlayer(profile)
        showProfileSummaryDialog(profile)
    }

    fun onDeletePlayerClicked(profile: PlayerProfile) {
        viewModelScope.launch {
            playerRepository.deletePlayer(profile)
        }
    }

    private fun showProfileSummaryDialog(profile: PlayerProfile) {
        val formattedSummary = """
            Игрок: ${profile.fullName}
            Пол: ${profile.gender.title}
            Курс: ${profile.course.title}
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

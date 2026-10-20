package com.example.buggame.ui.registration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = koinViewModel(),
    onRegistrationComplete: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    RegistrationContent(
        state = state,
        onNameChange = viewModel::onFullNameChanged,
        onGenderChange = viewModel::onGenderChanged,
        onCourseChange = viewModel::onCourseChanged,
        onDifficultyChange = viewModel::onDifficultyChanged,
        onDateChange = viewModel::onBirthDateChanged,
        onSubmitClick = viewModel::onSubmitClicked,
        onResultDialogDismiss = viewModel::onResultDialogDismissed,
        onStartClick = {
            viewModel.onResultDialogDismissed()
            onRegistrationComplete()
        }

    )
}
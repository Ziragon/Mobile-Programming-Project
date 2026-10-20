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
        onModeChange = viewModel::onModeChanged,
        onNameChange = viewModel::onFullNameChanged,
        onGenderChange = viewModel::onGenderChanged,
        onCourseChange = viewModel::onCourseChanged,
        onDateChange = viewModel::onBirthDateChanged,
        onSubmitClick = viewModel::onSubmitClicked,
        onSelectExistingPlayer = viewModel::onExistingPlayerSelected,
        onDeletePlayer = viewModel::onDeletePlayerClicked,
        onResultDialogDismiss = viewModel::onResultDialogDismissed,
        onStartClick = {
            viewModel.onResultDialogDismissed()
            onRegistrationComplete()
        }
    )
}

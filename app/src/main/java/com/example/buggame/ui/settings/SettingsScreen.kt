package com.example.buggame.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    SettingsContent(
        state = state,
        onGameSpeedChange = viewModel::onGameSpeedChanged,
        onMaxBugsChange = viewModel::onMaxBugsChanged,
        onBonusIntervalChange = viewModel::onBonusIntervalChanged,
        onRoundDurationChange = viewModel::onRoundDurationChanged,
        onSaveClick = viewModel::onSaveClicked,
        onSaveSnackbarDismissed = viewModel::onSaveSnackbarDismissed
    )
}
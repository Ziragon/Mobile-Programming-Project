package com.example.buggame.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    SettingsContent(
        state = state,
        onGameSpeedChange = viewModel::onGameSpeedChanged,
        onMaxBugsChange = viewModel::onMaxBugsChanged,
        onBonusIntervalChange = viewModel::onBonusIntervalChanged,
        onRoundDurationChange = viewModel::onRoundDurationChanged
    )
}

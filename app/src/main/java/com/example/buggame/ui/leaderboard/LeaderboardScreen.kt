package com.example.buggame.ui.leaderboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LeaderboardContent(
        state = state,
        onTabSelected = viewModel::onTabSelected,
        onClearScores = viewModel::onClearScoresClicked
    )
}

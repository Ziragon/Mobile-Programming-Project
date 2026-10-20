package com.example.buggame.ui.rules

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun RulesScreen(
    viewModel: RulesViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    RulesContent(
        state = state,
        onRetryClick = { viewModel.loadRules() }
    )
}
package com.example.buggame.ui.rules

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RulesScreen(
    viewModel: RulesViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    RulesContent(
        state = state,
        onRetryClick = { viewModel.loadRules() },
        onBackClick = onBackClick
    )
}
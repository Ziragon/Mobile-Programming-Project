package com.example.buggame.ui.authors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthorsScreen(
    viewModel: AuthorsViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    AuthorsContent(
        state = state,
        onBackClick = onBackClick
    )
}

package com.example.buggame.ui.authors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthorsScreen(
    viewModel: AuthorsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    AuthorsContent(
        state = state
    )
}

package com.example.buggame.ui.authors

import androidx.lifecycle.ViewModel
import com.example.buggame.R
import com.example.buggame.data.model.Author
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthorsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthorsState(isLoading = true))
    val uiState: StateFlow<AuthorsState> = _uiState.asStateFlow()

    init {
        loadAuthors()
    }

    private fun loadAuthors() {
        val authorsList = listOf(
            Author(
                id = 1,
                fullName = "Евгений Кривенышев",
                role = "Бизнес-логика и архитектура",
                photoResId = R.drawable.author_krivenyshev
            ),
            Author(
                id = 2,
                fullName = "Никита Шушаков",
                role = "Фронтенд и дизайн",
                photoResId = R.drawable.author_shushakov
            )
        )

        _uiState.value = AuthorsState(
            authors = authorsList,
            isLoading = false
        )
    }
}

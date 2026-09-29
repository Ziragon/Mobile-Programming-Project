package com.example.buggame.ui.authors

import com.example.buggame.data.model.Author

data class AuthorsState(
    val authors: List<Author> = emptyList(),
    val isLoading: Boolean = false
)

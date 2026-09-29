package com.example.buggame.ui.rules

data class RulesState(
    val htmlContent: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

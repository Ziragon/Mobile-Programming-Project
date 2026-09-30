package com.example.buggame.ui.settings

import com.example.buggame.data.model.GameSettings

data class SettingsState(
    val settings: GameSettings = GameSettings()
)
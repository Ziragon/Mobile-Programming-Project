package com.example.buggame.ui.settings

import androidx.compose.runtime.Composable

@Composable
fun SettingsContent(
    state: SettingsState,
    onGameSpeedChange: (Float) -> Unit,
    onMaxBugsChange: (Int) -> Unit,
    onBonusIntervalChange: (Int) -> Unit,
    onRoundDurationChange: (Int) -> Unit,
    onSaveClick: () -> Unit
) {}

package com.example.buggame.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.buggame.data.model.GameSettings

@Composable
fun SettingsContent(
    state: SettingsState,
    onGameSpeedChange: (Float) -> Unit,
    onMaxBugsChange: (Int) -> Unit,
    onBonusIntervalChange: (Int) -> Unit,
    onRoundDurationChange: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onSaveSnackbarDismissed: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isSavedSuccess) {
        if (state.isSavedSuccess) {
            snackbarHostState.showSnackbar("Настройки сохранены")
            onSaveSnackbarDismissed()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Text(
                text = "Настройки игры",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            SettingsSliderFloat(
                label = "Скорость игры",
                value = state.settings.gameSpeed,
                valueText = "×${"%.1f".format(state.settings.gameSpeed)}",
                range = 0.5f..3.0f,
                steps = 24,
                onValueChange = onGameSpeedChange
            )

            SettingsSliderInt(
                label = "Максимум тараканов на экране",
                value = state.settings.maxBugsOnScreen,
                range = 1..30,
                onValueChange = onMaxBugsChange
            )

            SettingsSliderInt(
                label = "Интервал появления бонусов (сек)",
                value = state.settings.bonusIntervalSeconds,
                range = 5..60,
                onValueChange = onBonusIntervalChange
            )

            SettingsSliderInt(
                label = "Длительность раунда (сек)",
                value = state.settings.roundDurationSeconds,
                range = 30..300,
                onValueChange = onRoundDurationChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }
    }
}

@Composable
private fun SettingsSliderFloat(
    label: String,
    value: Float,
    valueText: String,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Column {
        Text(
            text = "$label: $valueText",
            style = MaterialTheme.typography.labelLarge
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps
        )
    }
}

@Composable
private fun SettingsSliderInt(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column {
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.labelLarge
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = (range.last - range.first - 1).coerceAtLeast(0)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    SettingsContent(
        state = SettingsState(
            settings = GameSettings(
                gameSpeed = 1.5f,
                maxBugsOnScreen = 12,
                bonusIntervalSeconds = 20,
                roundDurationSeconds = 90
            )
        ),
        onGameSpeedChange = {},
        onMaxBugsChange = {},
        onBonusIntervalChange = {},
        onRoundDurationChange = {},
        onSaveClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentSavedPreview() {
    SettingsContent(
        state = SettingsState(
            settings = GameSettings(),
            isSavedSuccess = true
        ),
        onGameSpeedChange = {},
        onMaxBugsChange = {},
        onBonusIntervalChange = {},
        onRoundDurationChange = {},
        onSaveClick = {}
    )
}
package com.example.buggame.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.buggame.R
import com.example.buggame.data.model.GameSettings

@Composable
fun SettingsContent(
    state: SettingsState,
    onGameSpeedChange: (Float) -> Unit,
    onMaxBugsChange: (Int) -> Unit,
    onBonusIntervalChange: (Int) -> Unit,
    onRoundDurationChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.screen_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.section_spacing))
    ) {
        SettingsSliderFloat(
            label = "Скорость игры",
            value = state.settings.gameSpeed,
            valueText = "×${"%.1f".format(state.settings.gameSpeed)}",
            range = 0.5f..3.0f,
            steps = 24,
            onValueChange = onGameSpeedChange
        )

        HorizontalDivider()

        SettingsSliderInt(
            label = "Максимум тараканов на экране",
            value = state.settings.maxBugsOnScreen,
            range = 1..30,
            onValueChange = onMaxBugsChange
        )

        HorizontalDivider()

        SettingsSliderInt(
            label = "Интервал появления бонусов (сек)",
            value = state.settings.bonusIntervalSeconds,
            range = 5..60,
            onValueChange = onBonusIntervalChange
        )

        HorizontalDivider()

        SettingsSliderInt(
            label = "Длительность раунда (сек)",
            value = state.settings.roundDurationSeconds,
            range = 30..300,
            onValueChange = onRoundDurationChange
        )
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
        state = SettingsState(settings = GameSettings()),
        onGameSpeedChange = {},
        onMaxBugsChange = {},
        onBonusIntervalChange = {},
        onRoundDurationChange = {}
    )
}

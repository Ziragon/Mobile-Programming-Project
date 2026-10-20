package com.example.buggame.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.buggame.R
import com.example.buggame.data.model.getDifficultyName
import kotlin.math.roundToInt

@Composable
fun SettingsContent(
    state: SettingsState,
    onGameSpeedChange: (Float) -> Unit,
    onMaxBugsChange: (Int) -> Unit,
    onBonusIntervalChange: (Float) -> Unit,
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
            label = "Сложность игры",
            value = state.settings.gameSpeed,
            valueText = "${getDifficultyName(state.settings.gameSpeed)} (${state.settings.gameSpeed.toInt()})",
            range = 1f..5f,
            steps = 3,
            onValueChange = { onGameSpeedChange(it.roundToInt().toFloat()) }
        )

        HorizontalDivider()

        SettingsSliderInt(
            label = "Максимум тараканов на экране",
            value = state.settings.maxBugsOnScreen,
            range = 5..15,
            onValueChange = onMaxBugsChange
        )

        HorizontalDivider()

        SettingsSliderFloat(
            label = "Интервал появления бонусов",
            value = state.settings.bonusIntervalSeconds,
            valueText = "${"%.1f".format(state.settings.bonusIntervalSeconds)} сек",
            range = 5f..10f,
            steps = 9,
            onValueChange = { raw ->
                val rounded = (raw * 2).roundToInt() / 2f
                onBonusIntervalChange(rounded)
            }
        )

        HorizontalDivider()

        SettingsSliderInt(
            label = "Длительность раунда (сек)",
            value = state.settings.roundDurationSeconds,
            range = 10..60,
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

package com.example.buggame.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.buggame.R
import com.example.buggame.data.model.InsectSpawn
import com.example.buggame.ui.settings.SettingsViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()

    LaunchedEffect(settingsState.settings) {
        viewModel.updateSettings(settingsState.settings)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.falling_stars_background)
        )
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HudCard(label = "Очки", value = state.score.toString())
                HudCard(label = "Время", value = formatTime(state.timeLeftSeconds))
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            viewModel.onMissTapped()
                        }
                    }
            ) {
                val density = LocalDensity.current
                val areaWidthPx = with(density) { maxWidth.toPx() }
                val areaHeightPx = with(density) { maxHeight.toPx() }
                val insectSizeDp = 56.dp
                val insectSizePx = with(density) { insectSizeDp.toPx() }

                if (!state.isRunning && !state.isRoundOver) {
                    Text(
                        text = "Нажмите «Старт», чтобы начать раунд",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                state.insects.forEach { insect ->
                    key(insect.id) {
                        InsectView(
                            insect = insect,
                            areaWidthPx = areaWidthPx,
                            areaHeightPx = areaHeightPx,
                            insectSizeDp = insectSizeDp,
                            insectSizePx = insectSizePx,
                            speedPxPerSec = settingsState.settings.insectSpeedPxPerSec,
                            isRunning = state.isRunning,
                            onTapped = { viewModel.onInsectTapped(insect.id) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = viewModel::onStartClicked,
                    enabled = !state.isRunning,
                    modifier = Modifier.weight(1f)
                ) { Text("Старт") }

                OutlinedButton(
                    onClick = viewModel::onPauseClicked,
                    enabled = state.isRunning,
                    modifier = Modifier.weight(1f)
                ) { Text("Пауза") }

                OutlinedButton(
                    onClick = viewModel::onResetClicked,
                    modifier = Modifier.weight(1f)
                ) { Text("Сброс") }
            }
        }
    }

    if (state.isRoundOver) {
        RoundOverDialog(
            score = state.score,
            onDismiss = viewModel::onRoundOverDialogDismissed,
            onPlayAgain = {
                viewModel.onRoundOverDialogDismissed()
                viewModel.onResetClicked()
                viewModel.onStartClicked()
            }
        )
    }
}

@Composable
private fun InsectView(
    insect: InsectSpawn,
    areaWidthPx: Float,
    areaHeightPx: Float,
    insectSizeDp: Dp,
    insectSizePx: Float,
    speedPxPerSec: Float,
    isRunning: Boolean,
    onTapped: () -> Unit
) {
    val position = remember(insect.id) {
        Animatable(
            initialValue = Offset(
                x = Random.nextFloat() * (areaWidthPx - insectSizePx).coerceAtLeast(1f),
                y = Random.nextFloat() * (areaHeightPx - insectSizePx).coerceAtLeast(1f)
            ),
            typeConverter = Offset.VectorConverter
        )
    }

    val isRunningState = rememberUpdatedState(isRunning)

    LaunchedEffect(insect.id, areaWidthPx, areaHeightPx) {
        if (areaWidthPx <= insectSizePx || areaHeightPx <= insectSizePx) return@LaunchedEffect

        while (true) {
            if (!isRunningState.value) {
                delay(150)
                continue
            }

            val target = Offset(
                x = Random.nextFloat() * (areaWidthPx - insectSizePx),
                y = Random.nextFloat() * (areaHeightPx - insectSizePx)
            )
            val distance = (target - position.value).getDistance()
            val durationMs = ((distance / speedPxPerSec) * 1000)
                .toInt()
                .coerceIn(300, 4000)

            position.animateTo(
                targetValue = target,
                animationSpec = tween(durationMs, easing = LinearEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(position.value.x.roundToInt(), position.value.y.roundToInt())
            }
            .size(insectSizeDp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTapped
            )
    ) {
        Image(
            painter = painterResource(id = insect.type.iconRes),
            contentDescription = insect.type.contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun HudCard(label: String, value: String) {
    Card {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RoundOverDialog(
    score: Int,
    onDismiss: () -> Unit,
    onPlayAgain: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.extraLarge) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Раунд окончен",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ваш результат: $score очков",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(onClick = onPlayAgain) { Text("Играть снова") }
            }
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
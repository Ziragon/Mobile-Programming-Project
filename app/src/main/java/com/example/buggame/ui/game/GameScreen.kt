package com.example.buggame.ui.game

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
import kotlin.time.Duration.Companion.milliseconds
import org.koin.androidx.compose.koinViewModel

private const val ICON_VISUAL_SCALE_COMPENSATION = 1.2f

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun GameScreen(
    viewModel: GameViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
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
                    .clipToBounds()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            viewModel.onMissTapped()
                        }
                    }
            ) {
                val density = LocalDensity.current
                val areaWidthPx = with(density) { maxWidth.toPx() }
                val areaHeightPx = with(density) { maxHeight.toPx() }
                val insectSizeDp = 40.dp
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
                            baseSpeedPxPerSec = settingsState.settings.insectSpeedPxPerSec,
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
    baseSpeedPxPerSec: Float,
    isRunning: Boolean,
    onTapped: () -> Unit
) {
    val effectiveSpeed = baseSpeedPxPerSec * insect.speedMultiplier

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
                delay(150.milliseconds)
                continue
            }

            val target = Offset(
                x = Random.nextFloat() * (areaWidthPx - insectSizePx),
                y = Random.nextFloat() * (areaHeightPx - insectSizePx)
            )
            val distance = (target - position.value).getDistance()
            val durationMs = ((distance / effectiveSpeed) * 1000)
                .toInt()
                .coerceIn(200, 4000)

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
        if (insect.isBonus) {
            val infiniteTransition = rememberInfiniteTransition(label = "bonusPulse")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 0.9f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bonusAlpha"
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .scale(1.5f)
                    .background(
                        color = Color(0xFFFFD700).copy(alpha = pulseAlpha),
                        shape = CircleShape
                    )
            )
        }

        Image(
            painter = painterResource(id = insect.iconRes),
            contentDescription = insect.contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .scale(ICON_VISUAL_SCALE_COMPENSATION)
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
package com.example.buggame.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.buggame.R

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                if (!state.isRunning && !state.isRoundOver) {
                    Text(
                        text = "Нажмите «Старт», чтобы начать раунд",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                // TODO анимированные насекомые
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
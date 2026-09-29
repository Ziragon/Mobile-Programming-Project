package com.example.buggame.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(
    onRulesClick: () -> Unit,
    onAuthorsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Bug Game",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onPlayClick, modifier = Modifier.fillMaxWidth()) {
            Text("Играть")
        }
        Button(onClick = onRulesClick, modifier = Modifier.fillMaxWidth()) {
            Text("Правила игры")
        }
        Button(onClick = onAuthorsClick, modifier = Modifier.fillMaxWidth()) {
            Text("Об авторах")
        }
        Button(onClick = onSettingsClick, modifier = Modifier.fillMaxWidth()) {
            Text("Настройки")
        }
    }
}

@Composable
fun GamePlaceholderScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Игровой экран в разработке")
    }
}
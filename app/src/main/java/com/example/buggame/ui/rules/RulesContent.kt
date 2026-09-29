package com.example.buggame.ui.rules

import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun RulesContent(
    state: RulesState,
    onRetryClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }

            state.errorMessage != null -> {
                ErrorContent(
                    message = state.errorMessage,
                    onRetryClick = onRetryClick
                )
            }

            else -> {
                RulesWebView(htmlContent = state.htmlContent)
            }
        }
    }
}

@Composable
private fun RulesWebView(htmlContent: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = false
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                /* baseUrl = */ null,
                /* data = */ htmlContent,
                /* mimeType = */ "text/html",
                /* encoding = */ "UTF-8",
                /* historyUrl = */ null
            )
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun ErrorContent(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 48.dp)
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )

        Button(onClick = onRetryClick) {
            Text("Повторить попытку")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RulesContentLoadingPreview() {
    RulesContent(state = RulesState(isLoading = true))
}

@Preview(showBackground = true)
@Composable
private fun RulesContentErrorPreview() {
    RulesContent(
        state = RulesState(
            isLoading = false,
            errorMessage = "Ошибка при загрузке правил: File not found"
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun RulesContentSuccessPreview() {
    RulesContent(
        state = RulesState(
            isLoading = false,
            htmlContent = "<h1>Правила игры</h1><p>Тестовый текст правил для превью.</p>"
        )
    )
}
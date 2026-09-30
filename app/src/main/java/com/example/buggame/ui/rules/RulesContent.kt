package com.example.buggame.ui.rules

import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.buggame.R

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
            state.isLoading -> CircularProgressIndicator()

            state.errorMessage != null -> ErrorContent(
                message = state.errorMessage,
                onRetryClick = onRetryClick
            )

            else -> RulesWebView(htmlContent = state.htmlContent)
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
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
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
            .padding(dimensionResource(R.dimen.screen_padding)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.item_spacing))
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.section_spacing))
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
private fun RulesContentSuccessPreview() {
    RulesContent(
        state = RulesState(
            isLoading = false,
            htmlContent = "<h1>Правила игры</h1><p>Тестовый текст для превью.</p>"
        )
    )
}

package com.example.buggame.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.example.buggame.R

@Composable
fun BugGameTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = colorResource(R.color.accent_primary),
        onPrimary = colorResource(R.color.black),

        secondary = colorResource(R.color.accent_secondary),
        onSecondary = colorResource(R.color.black),

        background = colorResource(R.color.graphite_background),
        onBackground = colorResource(R.color.text_primary_dark),

        surface = colorResource(R.color.graphite_surface),
        onSurface = colorResource(R.color.text_primary_dark),

        surfaceVariant = colorResource(R.color.graphite_surface_variant),
        onSurfaceVariant = colorResource(R.color.text_secondary_dark),

        outline = colorResource(R.color.graphite_outline),

        error = colorResource(R.color.error_soft),
        onError = colorResource(R.color.black),

        surfaceTint = colorResource(R.color.graphite_outline)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
package com.iem1chr.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF60A5FA),
    secondary = androidx.compose.ui.graphics.Color(0xFF93C5FD),
    tertiary = androidx.compose.ui.graphics.Color(0xFFBBF7D0),
    background = androidx.compose.ui.graphics.Color(0xFF111827),
    surface = androidx.compose.ui.graphics.Color(0xFF1F2937),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White
)

@Composable
fun iem1ChrLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

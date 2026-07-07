package com.example.myapplication.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─── WashTrack Custom Blue & White Light Scheme ─────────────────────
private val WashTrackLightScheme = lightColorScheme(
    primary = AccentPrimary,
    onPrimary = TextInverse,
    primaryContainer = AccentGradientStart,
    onPrimaryContainer = TextInverse,
    secondary = AccentPrimaryHover,
    onSecondary = TextInverse,
    secondaryContainer = BgElevated,
    onSecondaryContainer = TextPrimary,
    tertiary = StatusDelivered,
    onTertiary = TextInverse,
    background = BgPrimary,
    onBackground = TextPrimary,
    surface = BgSecondary,
    onSurface = TextPrimary,
    surfaceVariant = BgElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderMedium,
    outlineVariant = BorderSubtle,
    error = ErrorRed,
    onError = TextInverse,
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // WashTrack always uses the premium light scheme (Blue & White) for parity with Web CSS
    val colorScheme = WashTrackLightScheme

    // Apply status bar color to match the app theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BgPrimary.toArgb()
            window.navigationBarColor = BgSecondary.toArgb()
            
            // Set light appearance so status bar icons are dark and readable on light background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
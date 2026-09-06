package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight
)

enum class AppThemeMode(val title: String) {
    SYSTEM("跟随系统"),
    LIGHT("浅色模式"),
    DARK("深色模式")
}

object ParcelThemeColors {
    val cardBackground: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1C2026) else Color(0xFFFFFFFF)

    val cardBorder: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF2E333D) else Color(0xFFE0E2EC)

    val textPrimary: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFF1F3F9) else Color(0xFF191C1C)

    val textSecondary: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFB0B3BC) else Color(0xFF44474E)

    val textMuted: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF8E9099) else Color(0xFF74777F)

    val containerVariant: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF262A33) else Color(0xFFF3F4F9)

    val containerVariantBorder: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF383E4A) else Color(0xFFE0E2EC)

    val accentBadgeBg: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF003366) else Color(0xFFD3E4FF)

    val accentBadgeText: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFD3E4FF) else Color(0xFF001C3B)

    val primaryActionBg: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFD3E4FF) else Color(0xFF001C3B)

    val primaryActionText: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF001C3B) else Color(0xFFFFFFFF)

    val bannerBg: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF382952) else Color(0xFFEADDFF)

    val bannerBorder: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF5A447F) else Color(0xFFD0BCFF)

    val bannerText: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFEADDFF) else Color(0xFF21005D)

    val bannerButton: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFD0BCFF) else Color(0xFF6750A4)

    val bannerButtonText: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF21005D) else Color(0xFFFFFFFF)

    val dialogSurface: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1E222A) else Color(0xFFFFFFFF)

    val successAccent: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF059669) else Color(0xFF10B981)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to keep our bold custom brand aesthetic
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

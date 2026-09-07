package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(val title: String) {
    SYSTEM("跟随系统"),
    LIGHT("浅色模式"),
    DARK("深色模式")
}

val LocalParcelColorScheme = staticCompositionLocalOf {
    getParcelColorScheme(AppColorPalette.MORANDI, false)
}

object ParcelThemeColors {
    val cardBackground: Color
        @Composable get() = LocalParcelColorScheme.current.cardBackground

    val cardBorder: Color
        @Composable get() = LocalParcelColorScheme.current.cardBorder

    val textPrimary: Color
        @Composable get() = LocalParcelColorScheme.current.textPrimary

    val textSecondary: Color
        @Composable get() = LocalParcelColorScheme.current.textSecondary

    val textMuted: Color
        @Composable get() = LocalParcelColorScheme.current.textMuted

    val containerVariant: Color
        @Composable get() = LocalParcelColorScheme.current.containerVariant

    val containerVariantBorder: Color
        @Composable get() = LocalParcelColorScheme.current.containerVariantBorder

    val accentBadgeBg: Color
        @Composable get() = LocalParcelColorScheme.current.accentBadgeBg

    val accentBadgeText: Color
        @Composable get() = LocalParcelColorScheme.current.accentBadgeText

    val primaryActionBg: Color
        @Composable get() = LocalParcelColorScheme.current.primaryActionBg

    val primaryActionText: Color
        @Composable get() = LocalParcelColorScheme.current.primaryActionText

    val bannerBg: Color
        @Composable get() = LocalParcelColorScheme.current.bannerBg

    val bannerBorder: Color
        @Composable get() = LocalParcelColorScheme.current.bannerBorder

    val bannerText: Color
        @Composable get() = LocalParcelColorScheme.current.bannerText

    val bannerButton: Color
        @Composable get() = LocalParcelColorScheme.current.bannerButton

    val bannerButtonText: Color
        @Composable get() = LocalParcelColorScheme.current.bannerButtonText

    val dialogSurface: Color
        @Composable get() = LocalParcelColorScheme.current.dialogSurface

    val successAccent: Color
        @Composable get() = LocalParcelColorScheme.current.successAccent

    val unpickedDot: Color
        @Composable get() = LocalParcelColorScheme.current.unpickedDot
}

@Composable
fun MyApplicationTheme(
    palette: AppColorPalette = AppColorPalette.MORANDI,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val parcelScheme = getParcelColorScheme(palette, darkTheme)

    val materialColorScheme = if (darkTheme) {
        darkColorScheme(
            primary = parcelScheme.primary,
            onPrimary = parcelScheme.onPrimary,
            primaryContainer = parcelScheme.primaryContainer,
            onPrimaryContainer = parcelScheme.onPrimaryContainer,
            secondary = parcelScheme.secondary,
            onSecondary = parcelScheme.onSecondary,
            background = parcelScheme.background,
            onBackground = parcelScheme.onBackground,
            surface = parcelScheme.surface,
            onSurface = parcelScheme.onSurface,
            surfaceVariant = parcelScheme.surfaceVariant,
            onSurfaceVariant = parcelScheme.onSurfaceVariant
        )
    } else {
        lightColorScheme(
            primary = parcelScheme.primary,
            onPrimary = parcelScheme.onPrimary,
            primaryContainer = parcelScheme.primaryContainer,
            onPrimaryContainer = parcelScheme.onPrimaryContainer,
            secondary = parcelScheme.secondary,
            onSecondary = parcelScheme.onSecondary,
            background = parcelScheme.background,
            onBackground = parcelScheme.onBackground,
            surface = parcelScheme.surface,
            onSurface = parcelScheme.onSurface,
            surfaceVariant = parcelScheme.surfaceVariant,
            onSurfaceVariant = parcelScheme.onSurfaceVariant
        )
    }

    CompositionLocalProvider(LocalParcelColorScheme provides parcelScheme) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = Typography,
            content = content
        )
    }
}

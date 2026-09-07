package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 完整的主题语义色彩定义接口，为全应用提供高品质的视觉色彩代币
 */
data class ParcelColorScheme(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val containerVariant: Color,
    val containerVariantBorder: Color,
    val accentBadgeBg: Color,
    val accentBadgeText: Color,
    val primaryActionBg: Color,
    val primaryActionText: Color,
    val bannerBg: Color,
    val bannerBorder: Color,
    val bannerText: Color,
    val bannerButton: Color,
    val bannerButtonText: Color,
    val dialogSurface: Color,
    val successAccent: Color,
    val unpickedDot: Color
)

/**
 * 高级艺术配色方案枚举
 */
enum class AppColorPalette(
    val title: String,
    val subtitle: String,
    val tag: String,
    val previewPrimary: Color,
    val previewAccent: Color,
    val previewBg: Color
) {
    MORANDI(
        title = "莫兰迪",
        subtitle = "低饱和静谧素雅 · 艺术灰调",
        tag = "沉静高雅",
        previewPrimary = Color(0xFF485758),
        previewAccent = Color(0xFFD4DDD7),
        previewBg = Color(0xFFF5F3F0)
    ),
    MONET(
        title = "莫奈花园",
        subtitle = "印象派睡莲倒影 · 柔雾水蓝与鸢尾紫",
        tag = "诗意浪漫",
        previewPrimary = Color(0xFF2D545E),
        previewAccent = Color(0xFFCFE7E5),
        previewBg = Color(0xFFF1F6F6)
    ),
    CLASSIC_NAVY(
        title = "极简深海",
        subtitle = "包豪斯深蓝理性 · 纯粹克制极简",
        tag = "经典沉稳",
        previewPrimary = Color(0xFF001C3B),
        previewAccent = Color(0xFFD3E4FF),
        previewBg = Color(0xFFF7F9FF)
    ),
    WABI_SABI(
        title = "乌木流沙",
        subtitle = "温润燕麦金沙 · 日系侘寂羊绒质感",
        tag = "温润质朴",
        previewPrimary = Color(0xFF463C35),
        previewAccent = Color(0xFFE7DFD3),
        previewBg = Color(0xFFF8F6F2)
    ),
    MISTY_PINE(
        title = "松风黛绿",
        subtitle = "宋瓷青竹雅韵 · 东方山峦静谧清气",
        tag = "东方禅意",
        previewPrimary = Color(0xFF264734),
        previewAccent = Color(0xFFCDE2D4),
        previewBg = Color(0xFFF2F6F3)
    )
}

/**
 * 根据所选的高级配色与深浅模式，返回精准匹配的语义调色板
 */
fun getParcelColorScheme(palette: AppColorPalette, darkTheme: Boolean): ParcelColorScheme {
    return when (palette) {
        AppColorPalette.MORANDI -> if (darkTheme) {
            ParcelColorScheme(
                primary = Color(0xFF9CB1AC),
                onPrimary = Color(0xFF1C2624),
                primaryContainer = Color(0xFF32413C),
                onPrimaryContainer = Color(0xFFD4DDD7),
                secondary = Color(0xFF8C776E),
                onSecondary = Color(0xFF1B1C1C),
                background = Color(0xFF1B1C1C),
                onBackground = Color(0xFFE6ECEB),
                surface = Color(0xFF242626),
                onSurface = Color(0xFFE6ECEB),
                surfaceVariant = Color(0xFF2D3030),
                onSurfaceVariant = Color(0xFFA8B2B0),
                cardBackground = Color(0xFF242626),
                cardBorder = Color(0xFF383B3B),
                textPrimary = Color(0xFFE6ECEB),
                textSecondary = Color(0xFFA8B2B0),
                textMuted = Color(0xFF76807E),
                containerVariant = Color(0xFF2A2D2D),
                containerVariantBorder = Color(0xFF3A3F3F),
                accentBadgeBg = Color(0xFF32413C),
                accentBadgeText = Color(0xFFD4DDD7),
                primaryActionBg = Color(0xFF9CB1AC),
                primaryActionText = Color(0xFF1C2624),
                bannerBg = Color(0xFF38312E),
                bannerBorder = Color(0xFF4E4440),
                bannerText = Color(0xFFE9DFD8),
                bannerButton = Color(0xFF8C776E),
                bannerButtonText = Color(0xFF1B1C1C),
                dialogSurface = Color(0xFF242626),
                successAccent = Color(0xFF74A08D),
                unpickedDot = Color(0xFFD66D6D)
            )
        } else {
            ParcelColorScheme(
                primary = Color(0xFF485758),
                onPrimary = Color(0xFFF7F6F4),
                primaryContainer = Color(0xFFD4DDD7),
                onPrimaryContainer = Color(0xFF2A3A35),
                secondary = Color(0xFF705E57),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFF5F3F0),
                onBackground = Color(0xFF2D3030),
                surface = Color(0xFFFCFAF8),
                onSurface = Color(0xFF2D3030),
                surfaceVariant = Color(0xFFEAE6E1),
                onSurfaceVariant = Color(0xFF575D5D),
                cardBackground = Color(0xFFFCFAF8),
                cardBorder = Color(0xFFDED8D0),
                textPrimary = Color(0xFF2D3030),
                textSecondary = Color(0xFF575D5D),
                textMuted = Color(0xFF899090),
                containerVariant = Color(0xFFEAE5DF),
                containerVariantBorder = Color(0xFFDDD6CE),
                accentBadgeBg = Color(0xFFD4DDD7),
                accentBadgeText = Color(0xFF2A3A35),
                primaryActionBg = Color(0xFF485758),
                primaryActionText = Color(0xFFF7F6F4),
                bannerBg = Color(0xFFE9DFD8),
                bannerBorder = Color(0xFFD7C7BE),
                bannerText = Color(0xFF45352F),
                bannerButton = Color(0xFF705E57),
                bannerButtonText = Color(0xFFFFFFFF),
                dialogSurface = Color(0xFFFCFAF8),
                successAccent = Color(0xFF4E7363),
                unpickedDot = Color(0xFFB34A4A)
            )
        }

        AppColorPalette.MONET -> if (darkTheme) {
            ParcelColorScheme(
                primary = Color(0xFF8EBDC0),
                onPrimary = Color(0xFF102629),
                primaryContainer = Color(0xFF1F3E41),
                onPrimaryContainer = Color(0xFFCFE7E5),
                secondary = Color(0xFF9473BC),
                onSecondary = Color(0xFF140E1C),
                background = Color(0xFF12181A),
                onBackground = Color(0xFFDFEFF1),
                surface = Color(0xFF182225),
                onSurface = Color(0xFFDFEFF1),
                surfaceVariant = Color(0xFF212F34),
                onSurfaceVariant = Color(0xFF9EC0C4),
                cardBackground = Color(0xFF182225),
                cardBorder = Color(0xFF283A3F),
                textPrimary = Color(0xFFDFEFF1),
                textSecondary = Color(0xFF9EC0C4),
                textMuted = Color(0xFF6C8E93),
                containerVariant = Color(0xFF1E2B2E),
                containerVariantBorder = Color(0xFF2F4348),
                accentBadgeBg = Color(0xFF1F3E41),
                accentBadgeText = Color(0xFFCFE7E5),
                primaryActionBg = Color(0xFF8EBDC0),
                primaryActionText = Color(0xFF102629),
                bannerBg = Color(0xFF2F2240),
                bannerBorder = Color(0xFF4A3566),
                bannerText = Color(0xFFEBE3F5),
                bannerButton = Color(0xFF9473BC),
                bannerButtonText = Color(0xFF140E1C),
                dialogSurface = Color(0xFF182225),
                successAccent = Color(0xFF4EC5B6),
                unpickedDot = Color(0xFFE26D7D)
            )
        } else {
            ParcelColorScheme(
                primary = Color(0xFF2D545E),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFCFE7E5),
                onPrimaryContainer = Color(0xFF143D3B),
                secondary = Color(0xFF6B4E91),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFF1F6F6),
                onBackground = Color(0xFF192A2E),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF192A2E),
                surfaceVariant = Color(0xFFE2EEEE),
                onSurfaceVariant = Color(0xFF415A60),
                cardBackground = Color(0xFFFFFFFF),
                cardBorder = Color(0xFFD0DFDF),
                textPrimary = Color(0xFF192A2E),
                textSecondary = Color(0xFF415A60),
                textMuted = Color(0xFF6E888E),
                containerVariant = Color(0xFFE3EDED),
                containerVariantBorder = Color(0xFFCCDEDE),
                accentBadgeBg = Color(0xFFCFE7E5),
                accentBadgeText = Color(0xFF143D3B),
                primaryActionBg = Color(0xFF2D545E),
                primaryActionText = Color(0xFFFFFFFF),
                bannerBg = Color(0xFFEBE3F5),
                bannerBorder = Color(0xFFD5C4E8),
                bannerText = Color(0xFF371E5A),
                bannerButton = Color(0xFF6B4E91),
                bannerButtonText = Color(0xFFFFFFFF),
                dialogSurface = Color(0xFFFFFFFF),
                successAccent = Color(0xFF268579),
                unpickedDot = Color(0xFFC04252)
            )
        }

        AppColorPalette.CLASSIC_NAVY -> if (darkTheme) {
            ParcelColorScheme(
                primary = Color(0xFFD3E4FF),
                onPrimary = Color(0xFF001C3B),
                primaryContainer = Color(0xFF003366),
                onPrimaryContainer = Color(0xFFD3E4FF),
                secondary = Color(0xFFD0BCFF),
                onSecondary = Color(0xFF21005D),
                background = Color(0xFF14171B),
                onBackground = Color(0xFFF1F3F9),
                surface = Color(0xFF1C2026),
                onSurface = Color(0xFFF1F3F9),
                surfaceVariant = Color(0xFF272C35),
                onSurfaceVariant = Color(0xFFB0B3BC),
                cardBackground = Color(0xFF1C2026),
                cardBorder = Color(0xFF2E333D),
                textPrimary = Color(0xFFF1F3F9),
                textSecondary = Color(0xFFB0B3BC),
                textMuted = Color(0xFF8E9099),
                containerVariant = Color(0xFF262A33),
                containerVariantBorder = Color(0xFF383E4A),
                accentBadgeBg = Color(0xFF003366),
                accentBadgeText = Color(0xFFD3E4FF),
                primaryActionBg = Color(0xFFD3E4FF),
                primaryActionText = Color(0xFF001C3B),
                bannerBg = Color(0xFF382952),
                bannerBorder = Color(0xFF5A447F),
                bannerText = Color(0xFFEADDFF),
                bannerButton = Color(0xFFD0BCFF),
                bannerButtonText = Color(0xFF21005D),
                dialogSurface = Color(0xFF1E222A),
                successAccent = Color(0xFF059669),
                unpickedDot = Color(0xFFFF6B6B)
            )
        } else {
            ParcelColorScheme(
                primary = Color(0xFF001C3B),
                onPrimary = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFD3E4FF),
                onPrimaryContainer = Color(0xFF001C3B),
                secondary = Color(0xFF6750A4),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFF7F9FF),
                onBackground = Color(0xFF191C1C),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF191C1C),
                surfaceVariant = Color(0xFFF3F4F9),
                onSurfaceVariant = Color(0xFF44474E),
                cardBackground = Color(0xFFFFFFFF),
                cardBorder = Color(0xFFE0E2EC),
                textPrimary = Color(0xFF191C1C),
                textSecondary = Color(0xFF44474E),
                textMuted = Color(0xFF74777F),
                containerVariant = Color(0xFFF3F4F9),
                containerVariantBorder = Color(0xFFE0E2EC),
                accentBadgeBg = Color(0xFFD3E4FF),
                accentBadgeText = Color(0xFF001C3B),
                primaryActionBg = Color(0xFF001C3B),
                primaryActionText = Color(0xFFFFFFFF),
                bannerBg = Color(0xFFEADDFF),
                bannerBorder = Color(0xFFD0BCFF),
                bannerText = Color(0xFF21005D),
                bannerButton = Color(0xFF6750A4),
                bannerButtonText = Color(0xFFFFFFFF),
                dialogSurface = Color(0xFFFFFFFF),
                successAccent = Color(0xFF10B981),
                unpickedDot = Color(0xFFBA1A1A)
            )
        }

        AppColorPalette.WABI_SABI -> if (darkTheme) {
            ParcelColorScheme(
                primary = Color(0xFFD2C0AF),
                onPrimary = Color(0xFF231D18),
                primaryContainer = Color(0xFF3D3328),
                onPrimaryContainer = Color(0xFFE7DFD3),
                secondary = Color(0xFF9F8065),
                onSecondary = Color(0xFF1B1612),
                background = Color(0xFF1A1816),
                onBackground = Color(0xFFEFE8DF),
                surface = Color(0xFF23201D),
                onSurface = Color(0xFFEFE8DF),
                surfaceVariant = Color(0xFF2F2A25),
                onSurfaceVariant = Color(0xFFBCB1A3),
                cardBackground = Color(0xFF23201D),
                cardBorder = Color(0xFF3B342D),
                textPrimary = Color(0xFFEFE8DF),
                textSecondary = Color(0xFFBCB1A3),
                textMuted = Color(0xFF897F73),
                containerVariant = Color(0xFF2B2622),
                containerVariantBorder = Color(0xFF3E3730),
                accentBadgeBg = Color(0xFF3D3328),
                accentBadgeText = Color(0xFFE7DFD3),
                primaryActionBg = Color(0xFFD2C0AF),
                primaryActionText = Color(0xFF231D18),
                bannerBg = Color(0xFF382E24),
                bannerBorder = Color(0xFF504133),
                bannerText = Color(0xFFEFE4D7),
                bannerButton = Color(0xFF9F8065),
                bannerButtonText = Color(0xFF1B1612),
                dialogSurface = Color(0xFF23201D),
                successAccent = Color(0xFF83A68B),
                unpickedDot = Color(0xFFE06D5C)
            )
        } else {
            ParcelColorScheme(
                primary = Color(0xFF463C35),
                onPrimary = Color(0xFFFBF9F5),
                primaryContainer = Color(0xFFE7DFD3),
                onPrimaryContainer = Color(0xFF3A2E24),
                secondary = Color(0xFF7B5F47),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFF8F6F2),
                onBackground = Color(0xFF2E2722),
                surface = Color(0xFFFFFDF9),
                onSurface = Color(0xFF2E2722),
                surfaceVariant = Color(0xFFEFECE5),
                onSurfaceVariant = Color(0xFF5C534B),
                cardBackground = Color(0xFFFFFDF9),
                cardBorder = Color(0xFFE2DDD4),
                textPrimary = Color(0xFF2E2722),
                textSecondary = Color(0xFF5C534B),
                textMuted = Color(0xFF8F847B),
                containerVariant = Color(0xFFEFECE5),
                containerVariantBorder = Color(0xFFDCD6CB),
                accentBadgeBg = Color(0xFFE7DFD3),
                accentBadgeText = Color(0xFF3A2E24),
                primaryActionBg = Color(0xFF463C35),
                primaryActionText = Color(0xFFFBF9F5),
                bannerBg = Color(0xFFEFE4D7),
                bannerBorder = Color(0xFFDECBB9),
                bannerText = Color(0xFF493424),
                bannerButton = Color(0xFF7B5F47),
                bannerButtonText = Color(0xFFFFFFFF),
                dialogSurface = Color(0xFFFFFDF9),
                successAccent = Color(0xFF597860),
                unpickedDot = Color(0xFFB54536)
            )
        }

        AppColorPalette.MISTY_PINE -> if (darkTheme) {
            ParcelColorScheme(
                primary = Color(0xFF96C7AC),
                onPrimary = Color(0xFF0E2015),
                primaryContainer = Color(0xFF1E3A28),
                onPrimaryContainer = Color(0xFFCDE2D4),
                secondary = Color(0xFF759E6D),
                onSecondary = Color(0xFF111A0F),
                background = Color(0xFF121A14),
                onBackground = Color(0xFFDFEFE4),
                surface = Color(0xFF1A231D),
                onSurface = Color(0xFFDFEFE4),
                surfaceVariant = Color(0xFF233027),
                onSurfaceVariant = Color(0xFFA1C1AC),
                cardBackground = Color(0xFF1A231D),
                cardBorder = Color(0xFF283A2E),
                textPrimary = Color(0xFFDFEFE4),
                textSecondary = Color(0xFFA1C1AC),
                textMuted = Color(0xFF6F8F7B),
                containerVariant = Color(0xFF212D24),
                containerVariantBorder = Color(0xFF304235),
                accentBadgeBg = Color(0xFF1E3A28),
                accentBadgeText = Color(0xFFCDE2D4),
                primaryActionBg = Color(0xFF96C7AC),
                primaryActionText = Color(0xFF0E2015),
                bannerBg = Color(0xFF243320),
                bannerBorder = Color(0xFF384C32),
                bannerText = Color(0xFFE7EFE3),
                bannerButton = Color(0xFF759E6D),
                bannerButtonText = Color(0xFF111A0F),
                dialogSurface = Color(0xFF1A231D),
                successAccent = Color(0xFF47BE77),
                unpickedDot = Color(0xFFDE6772)
            )
        } else {
            ParcelColorScheme(
                primary = Color(0xFF264734),
                onPrimary = Color(0xFFF2F6F3),
                primaryContainer = Color(0xFFCDE2D4),
                onPrimaryContainer = Color(0xFF153723),
                secondary = Color(0xFF4A6C44),
                onSecondary = Color(0xFFFFFFFF),
                background = Color(0xFFF2F6F3),
                onBackground = Color(0xFF1C2A21),
                surface = Color(0xFFFCFEFC),
                onSurface = Color(0xFF1C2A21),
                surfaceVariant = Color(0xFFE3ECE6),
                onSurfaceVariant = Color(0xFF43584A),
                cardBackground = Color(0xFFFCFEFC),
                cardBorder = Color(0xFFCEDED4),
                textPrimary = Color(0xFF1C2A21),
                textSecondary = Color(0xFF43584A),
                textMuted = Color(0xFF728B7B),
                containerVariant = Color(0xFFE4EDE6),
                containerVariantBorder = Color(0xFFCCDCD0),
                accentBadgeBg = Color(0xFFCDE2D4),
                accentBadgeText = Color(0xFF153723),
                primaryActionBg = Color(0xFF264734),
                primaryActionText = Color(0xFFF2F6F3),
                bannerBg = Color(0xFFE7EFE3),
                bannerBorder = Color(0xFFCBDDC7),
                bannerText = Color(0xFF294324),
                bannerButton = Color(0xFF4A6C44),
                bannerButtonText = Color(0xFFFFFFFF),
                dialogSurface = Color(0xFFFCFEFC),
                successAccent = Color(0xFF26854F),
                unpickedDot = Color(0xFFB83E48)
            )
        }
    }
}

// 保持历史引用的常量兼容定义
val PrimaryLight = Color(0xFF001C3B)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFD3E4FF)
val OnPrimaryContainerLight = Color(0xFF001C3B)

val SecondaryLight = Color(0xFF6750A4)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFEADDFF)
val OnSecondaryContainerLight = Color(0xFF21005D)

val TertiaryLight = Color(0xFF44474E)
val OnTertiaryLight = Color(0xFFFFFFFF)

val BackgroundLight = Color(0xFFF7F9FF)
val OnBackgroundLight = Color(0xFF191C1C)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF191C1C)
val SurfaceVariantLight = Color(0xFFF3F4F9)
val OnSurfaceVariantLight = Color(0xFF44474E)

val PrimaryDark = Color(0xFFD3E4FF)
val OnPrimaryDark = Color(0xFF001C3B)
val PrimaryContainerDark = Color(0xFF003366)
val OnPrimaryContainerDark = Color(0xFFD3E4FF)

val SecondaryDark = Color(0xFFD0BCFF)
val OnSecondaryDark = Color(0xFF381E72)
val SecondaryContainerDark = Color(0xFF4F378B)
val OnSecondaryContainerDark = Color(0xFFEADDFF)

val TertiaryDark = Color(0xFFC4C6D0)
val OnTertiaryDark = Color(0xFF2E3137)

val BackgroundDark = Color(0xFF14171B)
val OnBackgroundDark = Color(0xFFE2E2E6)
val SurfaceDark = Color(0xFF1C2026)
val OnSurfaceDark = Color(0xFFE2E2E6)
val SurfaceVariantDark = Color(0xFF272C35)
val OnSurfaceVariantDark = Color(0xFFC4C6D0)

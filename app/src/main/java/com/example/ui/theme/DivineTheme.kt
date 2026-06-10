package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class ThemePreset(val displayName: String) {
    NATURAL_TONES("Natural Tones"),
    SACRED_SAFFRON("Sacred Saffron"),
    LOTUS_PINK("Lotus Pink"),
    TULSI_GREEN("Tulsi Green"),
    GANGA_BLUE("Ganga Blue"),
    HIMALAYAN_WHITE("Himalayan-White"),
    TEMPLE_SAND("Temple Sand"),
    DIVINE_PURPLE("Divine Purple"),
    PEACOCK_BLUE("Peacock Blue"),
    GOLDEN_DHARMA("Golden Dharma"),
    SUNSET_PRAYER("Sunset Prayer")
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK, AMOLED
}

// Custom theme state holder for dynamic instant switching
class ThemeManager(
    initialPreset: ThemePreset = ThemePreset.NATURAL_TONES,
    initialMode: ThemeMode = ThemeMode.SYSTEM
) {
    var currentPreset by mutableStateOf(initialPreset)
    var currentMode by mutableStateOf(initialMode)
}

val LocalThemeManager = staticCompositionLocalOf { ThemeManager() }

@Composable
fun DivineHarmonyTheme(
    themeManager: ThemeManager,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeManager.currentMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
    }
    
    val isAmoled = themeManager.currentMode == ThemeMode.AMOLED

    val colorScheme = when (themeManager.currentPreset) {
        ThemePreset.NATURAL_TONES -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFFF9933),
                onPrimary = Color(0xFF2B1000),
                primaryContainer = Color(0xFFB35900),
                secondary = Color(0xFF8CAF95),
                background = if (isAmoled) Color.Black else Color(0xFF1B1611),
                surface = if (isAmoled) Color.Black else Color(0xFF251F19),
                onBackground = Color(0xFFF7ECE1),
                onSurface = Color(0xFFF7ECE1),
                surfaceVariant = Color(0xFF3B2F25),
                onSurfaceVariant = Color(0xFFE5CCB3)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFFFF9933),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFFE5CC),
                secondary = Color(0xFF4A7C59),
                background = Color(0xFFFDF8F1),
                surface = Color(0xFFFBF4EA),
                onBackground = Color(0xFF1E293B),
                onSurface = Color(0xFF4A3728),
                surfaceVariant = Color(0xFFF5E6D3),
                onSurfaceVariant = Color(0xFF8B4513)
            )
        }

        ThemePreset.SACRED_SAFFRON -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFFFAB91),
                onPrimary = Color(0xFF4E1500),
                primaryContainer = Color(0xFFD84315),
                secondary = Color(0xFFFFCCbc),
                background = if (isAmoled) Color.Black else Color(0xFF1E100A),
                surface = if (isAmoled) Color.Black else Color(0xFF281710),
                onBackground = Color(0xFFFFEBE5),
                onSurface = Color(0xFFFFEBE5),
                surfaceVariant = Color(0xFF38231A),
                onSurfaceVariant = Color(0xFFFFCCBC)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFFE65100),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFFE0B2),
                secondary = Color(0xFFFF7043),
                background = Color(0xFFFFFBF7),
                surface = Color(0xFFFFF2E6),
                onBackground = Color(0xFF210F00),
                onSurface = Color(0xFF210F00),
                surfaceVariant = Color(0xFFFFE5D9),
                onSurfaceVariant = Color(0xFF5D3F33)
            )
        }
        
        ThemePreset.LOTUS_PINK -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFF48FB1),
                onPrimary = Color(0xFF4C001F),
                primaryContainer = Color(0xFFEC407A),
                secondary = Color(0xFFF8BBD0),
                background = if (isAmoled) Color.Black else Color(0xFF1F0D14),
                surface = if (isAmoled) Color.Black else Color(0xFF2B141C),
                onBackground = Color(0xFFFFECEF),
                onSurface = Color(0xFFFFECEF)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFFD81B60),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFCE4EC),
                secondary = Color(0xFFEC407A),
                background = Color(0xFFFFF7F9),
                surface = Color(0xFFFCE8EE),
                onBackground = Color(0xFF2C0011),
                onSurface = Color(0xFF2C0011)
            )
        }

        ThemePreset.TULSI_GREEN -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFF81C784),
                onPrimary = Color(0xFF00330C),
                primaryContainer = Color(0xFF2E7D32),
                secondary = Color(0xFFC8E6C9),
                background = if (isAmoled) Color.Black else Color(0xFF0F1B11),
                surface = if (isAmoled) Color.Black else Color(0xFF17291A),
                onBackground = Color(0xFFE8F5E9),
                onSurface = Color(0xFFE8F5E9)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF1B5E20),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFE8F5E9),
                secondary = Color(0xFF4CAF50),
                background = Color(0xFFF9FDF9),
                surface = Color(0xFFECF7EC),
                onBackground = Color(0xFF002204),
                onSurface = Color(0xFF002204)
            )
        }

        ThemePreset.GANGA_BLUE -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFF4FC3F7),
                onPrimary = Color(0xFF00354E),
                primaryContainer = Color(0xFF0288D1),
                secondary = Color(0xFFB3E5FC),
                background = if (isAmoled) Color.Black else Color(0xFF09161F),
                surface = if (isAmoled) Color.Black else Color(0xFF11222E),
                onBackground = Color(0xFFE1F5FE),
                onSurface = Color(0xFFE1F5FE)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF01579B),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFE1F5FE),
                secondary = Color(0xFF0288D1),
                background = Color(0xFFF7FBFE),
                surface = Color(0xFFE9F5FC),
                onBackground = Color(0xFF001E35),
                onSurface = Color(0xFF001E35)
            )
        }

        ThemePreset.HIMALAYAN_WHITE -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFCFD8DC),
                onPrimary = Color(0xFF1A2327),
                primaryContainer = Color(0xFF455A64),
                secondary = Color(0xFFECEFF1),
                background = if (isAmoled) Color.Black else Color(0xFF1A1D20),
                surface = if (isAmoled) Color.Black else Color(0xFF222629),
                onBackground = Color(0xFFECEFF1),
                onSurface = Color(0xFFECEFF1)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF37474F),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFECEFF1),
                secondary = Color(0xFF607D8B),
                background = Color(0xFFF8F9FA),
                surface = Color(0xFFECEFF1),
                onBackground = Color(0xFF102027),
                onSurface = Color(0xFF102027)
            )
        }

        ThemePreset.TEMPLE_SAND -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFD7CCC8),
                onPrimary = Color(0xFF2D1510),
                primaryContainer = Color(0xFF5D4037),
                secondary = Color(0xFFEFEBE9),
                background = if (isAmoled) Color.Black else Color(0xFF1C1311),
                surface = if (isAmoled) Color.Black else Color(0xFF261D1A),
                onBackground = Color(0xFFFDFBFB),
                onSurface = Color(0xFFFDFBFB)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF4E342E),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFD7CCC8),
                secondary = Color(0xFF8D6E63),
                background = Color(0xFFFBF8F7),
                surface = Color(0xFFF5EBE6),
                onBackground = Color(0xFF2A1410),
                onSurface = Color(0xFF2A1410)
            )
        }

        ThemePreset.DIVINE_PURPLE -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFE1BEE7),
                onPrimary = Color(0xFF31004A),
                primaryContainer = Color(0xFF7B1FA2),
                secondary = Color(0xFFF3E5F5),
                background = if (isAmoled) Color.Black else Color(0xFF17091B),
                surface = if (isAmoled) Color.Black else Color(0xFF24112B),
                onBackground = Color(0xFFFAEDFD),
                onSurface = Color(0xFFFAEDFD)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF4A148C),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFF3E5F5),
                secondary = Color(0xFF9C27B0),
                background = Color(0xFFFCF7FD),
                surface = Color(0xFFF5E5F7),
                onBackground = Color(0xFF1C003C),
                onSurface = Color(0xFF1C003C)
            )
        }

        ThemePreset.PEACOCK_BLUE -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFF80CBC4),
                onPrimary = Color(0xFF00302D),
                primaryContainer = Color(0xFF00695C),
                secondary = Color(0xFFE0F2F1),
                background = if (isAmoled) Color.Black else Color(0xFF091C1B),
                surface = if (isAmoled) Color.Black else Color(0xFF112B29),
                onBackground = Color(0xFFE0F2F1),
                onSurface = Color(0xFFE0F2F1)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF004D40),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFE0F2F1),
                secondary = Color(0xFF00897B),
                background = Color(0xFFF6FBFB),
                surface = Color(0xFFE2F0EF),
                onBackground = Color(0xFF001C17),
                onSurface = Color(0xFF001C17)
            )
        }

        ThemePreset.GOLDEN_DHARMA -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFFFEE58),
                onPrimary = Color(0xFF373000),
                primaryContainer = Color(0xFFF57F17),
                secondary = Color(0xFFFFF9C4),
                background = if (isAmoled) Color.Black else Color(0xFF1B1A0A),
                surface = if (isAmoled) Color.Black else Color(0xFF292712),
                onBackground = Color(0xFFFFFEE5),
                onSurface = Color(0xFFFFFEE5)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFFF57F17),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFFFFF9C4),
                secondary = Color(0xFFFFB300),
                background = Color(0xFFFFFFFA),
                surface = Color(0xFFFFFDEF),
                onBackground = Color(0xFF262200),
                onSurface = Color(0xFF262200)
            )
        }

        ThemePreset.SUNSET_PRAYER -> if (isDark) {
            darkColorScheme(
                primary = Color(0xFFFFCC80),
                onPrimary = Color(0xFF4A2600),
                primaryContainer = Color(0xFFE65100),
                secondary = Color(0xFFFFE0B2),
                background = if (isAmoled) Color.Black else Color(0xFF20130F),
                surface = if (isAmoled) Color.Black else Color(0xFF2D1C16),
                onBackground = Color(0xFFFFEFEB),
                onSurface = Color(0xFFFFEFEB)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFFE65100),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFFE0B2),
                secondary = Color(0xFFF57C00),
                background = Color(0xFFFFF9F6),
                surface = Color(0xFFFFF0E6),
                onBackground = Color(0xFF2F1206),
                onSurface = Color(0xFF2F1206)
            )
        }
    }

    CompositionLocalProvider(
        LocalThemeManager provides themeManager
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

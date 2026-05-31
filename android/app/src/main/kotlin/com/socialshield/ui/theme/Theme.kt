package com.socialshield.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── Cybersecurity Color Palette ─────────────────────────────────────────────

val NeonBlue = Color(0xFF2563EB) // Royal Blue
val NeonPurple = Color(0xFF7C3AED) // Deep Purple
val NeonCyan = Color(0xFF059669) // Emerald
val NeonPink = Color(0xFFDB2777) // Pink
val DeepBlack = Color(0xFFFFFFFF) // White background (names kept to avoid breaking refs)
val DarkSurface = Color(0xFFF8FAFC) // Light Gray Surface
val DarkCard = Color(0xFFF1F5F9) // Light Card
val DarkElevated = Color(0xFFE2E8F0) // Elevated Light Card
val GlassWhite = Color(0x1A000000) // Dark Glass (inverted)
val GlassBorder = Color(0x26000000) // Dark Glass Border (inverted)

// Risk colors
val RiskHigh = Color(0xFFFF3B3B)
val RiskMedium = Color(0xFFFFB800)
val RiskLow = Color(0xFF06FFA5)

val VerdictReal = Color(0xFF06FFA5)
val VerdictFake = Color(0xFFFF3B3B)
val VerdictSuspicious = Color(0xFFFFB800)

// ─── Dark Color Scheme ────────────────────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary = NeonBlue,
    onPrimary = DeepBlack,
    primaryContainer = Color(0xFF001F3D),
    onPrimaryContainer = NeonBlue,
    secondary = NeonPurple,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF2A1060),
    onSecondaryContainer = NeonPurple,
    tertiary = NeonCyan,
    onTertiary = DeepBlack,
    background = DeepBlack,
    onBackground = Color(0xFF0F172A),
    surface = DarkSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = DarkCard,
    onSurfaceVariant = Color(0xFFB0B0D0),
    outline = GlassBorder,
    error = RiskHigh,
    onError = Color(0xFF0F172A)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0066CC),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFFD6EAFF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF6B4EBE),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFFECE0FF),
    onSecondaryContainer = Color(0xFF1E005A),
    tertiary = Color(0xFF007A57),
    onTertiary = Color(0xFF0F172A),
    background = Color(0xFFF0F4FF),
    onBackground = Color(0xFF0A0A1A),
    surface = Color(0xFF0F172A),
    onSurface = Color(0xFF0A0A1A),
    surfaceVariant = Color(0xFFE8EEFF),
    onSurfaceVariant = Color(0xFF44444F),
    outline = Color(0xFFCCCCDD),
    error = Color(0xFFCC0000),
    onError = Color(0xFF0F172A)
)

// ─── Typography ───────────────────────────────────────────────────────────────

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 57.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 45.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
    )
)

object ThemeState {
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
    }
}

@Composable
fun SocialShieldTheme(
    darkTheme: Boolean = ThemeState.isDarkMode.collectAsState().value,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

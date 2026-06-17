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
import androidx.compose.runtime.ReadOnlyComposable

// ─── Cybersecurity Color Palette ─────────────────────────────────────────────

val NeonBlue: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == Color(0xFFFFF6F0)) Color(0xFFD6A77A) else Color(0xFF38BDF8)

val NeonPurple: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == Color(0xFFFFF6F0)) Color(0xFFF4C9A8) else Color(0xFF60A5FA)

val NeonCyan: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == Color(0xFFFFF6F0)) Color(0xFFD6A77A) else Color(0xFF7DD3FC)

val NeonPink: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == Color(0xFFFFF6F0)) Color(0xFFF4C9A8) else Color(0xFFF472B6)

// Compatibility getters for theme-based colors
val DeepBlack: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

val DarkSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val DarkCard: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant

val DarkElevated: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant

val GlassWhite: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

val GlassBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)

val ContentColor: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onBackground

// Risk colors
val RiskHigh = Color(0xFFFF3B3B)
val RiskMedium = Color(0xFFFFB800)
val RiskLow = Color(0xFF00E676) // Premium bright green

val VerdictReal = Color(0xFF00E676)
val VerdictFake = Color(0xFFFF3B3B)
val VerdictSuspicious = Color(0xFFFFB800)

// ─── Dark Color Scheme ────────────────────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF0F172A),
    onPrimaryContainer = Color(0xFF38BDF8),
    secondary = Color(0xFF60A5FA),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFF60A5FA),
    tertiary = Color(0xFF7DD3FC),
    onTertiary = Color(0xFF0F172A),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0x26FFFFFF),
    error = RiskHigh,
    onError = Color(0xFFFFFFFF)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD6A77A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFF6F0),
    onPrimaryContainer = Color(0xFF2D2D2D),
    secondary = Color(0xFFF4C9A8),
    onSecondary = Color(0xFF2D2D2D),
    secondaryContainer = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF2D2D2D),
    tertiary = Color(0xFFF4C9A8),
    onTertiary = Color(0xFF2D2D2D),
    background = Color(0xFFFFF6F0),
    onBackground = Color(0xFF2D2D2D),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF2D2D2D),
    surfaceVariant = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0x26000000),
    error = Color(0xFFCC0000),
    onError = Color(0xFFFFFFFF)
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

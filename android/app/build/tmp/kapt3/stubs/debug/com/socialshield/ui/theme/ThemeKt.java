package com.socialshield.ui.theme;

import androidx.compose.material3.*;
import androidx.compose.runtime.Composable;
import kotlinx.coroutines.flow.StateFlow;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b \n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a%\u0010*\u001a\u00020+2\b\b\u0002\u0010,\u001a\u00020-2\u0011\u0010.\u001a\r\u0012\u0004\u0012\u00020+0/\u00a2\u0006\u0002\b0H\u0007\"\u0011\u0010\u0000\u001a\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003\"\u0013\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0006\u0010\u0007\"\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0013\u0010\u000b\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\f\u0010\u0007\"\u0013\u0010\r\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u000e\u0010\u0007\"\u0013\u0010\u000f\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0010\u0010\u0007\"\u0013\u0010\u0011\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0012\u0010\u0007\"\u0013\u0010\u0013\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0014\u0010\u0007\"\u000e\u0010\u0015\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0013\u0010\u0016\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0017\u0010\u0007\"\u0013\u0010\u0018\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0019\u0010\u0007\"\u0013\u0010\u001a\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u001b\u0010\u0007\"\u0013\u0010\u001c\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u001d\u0010\u0007\"\u0013\u0010\u001e\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u001f\u0010\u0007\"\u0013\u0010 \u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b!\u0010\u0007\"\u0013\u0010\"\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b#\u0010\u0007\"\u0013\u0010$\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b%\u0010\u0007\"\u0013\u0010&\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\'\u0010\u0007\"\u0013\u0010(\u001a\u00020\u0005\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b)\u0010\u0007\u00a8\u00061"}, d2 = {"AppTypography", "Landroidx/compose/material3/Typography;", "getAppTypography", "()Landroidx/compose/material3/Typography;", "DarkCard", "Landroidx/compose/ui/graphics/Color;", "getDarkCard", "()J", "J", "DarkColorScheme", "Landroidx/compose/material3/ColorScheme;", "DarkElevated", "getDarkElevated", "DarkSurface", "getDarkSurface", "DeepBlack", "getDeepBlack", "GlassBorder", "getGlassBorder", "GlassWhite", "getGlassWhite", "LightColorScheme", "NeonBlue", "getNeonBlue", "NeonCyan", "getNeonCyan", "NeonPink", "getNeonPink", "NeonPurple", "getNeonPurple", "RiskHigh", "getRiskHigh", "RiskLow", "getRiskLow", "RiskMedium", "getRiskMedium", "VerdictFake", "getVerdictFake", "VerdictReal", "getVerdictReal", "VerdictSuspicious", "getVerdictSuspicious", "SocialShieldTheme", "", "darkTheme", "", "content", "Lkotlin/Function0;", "Landroidx/compose/runtime/Composable;", "app_debug"})
public final class ThemeKt {
    private static final long NeonBlue = 0L;
    private static final long NeonPurple = 0L;
    private static final long NeonCyan = 0L;
    private static final long NeonPink = 0L;
    private static final long DeepBlack = 0L;
    private static final long DarkSurface = 0L;
    private static final long DarkCard = 0L;
    private static final long DarkElevated = 0L;
    private static final long GlassWhite = 0L;
    private static final long GlassBorder = 0L;
    private static final long RiskHigh = 0L;
    private static final long RiskMedium = 0L;
    private static final long RiskLow = 0L;
    private static final long VerdictReal = 0L;
    private static final long VerdictFake = 0L;
    private static final long VerdictSuspicious = 0L;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.compose.material3.ColorScheme DarkColorScheme = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.compose.material3.ColorScheme LightColorScheme = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.compose.material3.Typography AppTypography = null;
    
    public static final long getNeonBlue() {
        return 0L;
    }
    
    public static final long getNeonPurple() {
        return 0L;
    }
    
    public static final long getNeonCyan() {
        return 0L;
    }
    
    public static final long getNeonPink() {
        return 0L;
    }
    
    public static final long getDeepBlack() {
        return 0L;
    }
    
    public static final long getDarkSurface() {
        return 0L;
    }
    
    public static final long getDarkCard() {
        return 0L;
    }
    
    public static final long getDarkElevated() {
        return 0L;
    }
    
    public static final long getGlassWhite() {
        return 0L;
    }
    
    public static final long getGlassBorder() {
        return 0L;
    }
    
    public static final long getRiskHigh() {
        return 0L;
    }
    
    public static final long getRiskMedium() {
        return 0L;
    }
    
    public static final long getRiskLow() {
        return 0L;
    }
    
    public static final long getVerdictReal() {
        return 0L;
    }
    
    public static final long getVerdictFake() {
        return 0L;
    }
    
    public static final long getVerdictSuspicious() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final androidx.compose.material3.Typography getAppTypography() {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SocialShieldTheme(boolean darkTheme, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> content) {
    }
}
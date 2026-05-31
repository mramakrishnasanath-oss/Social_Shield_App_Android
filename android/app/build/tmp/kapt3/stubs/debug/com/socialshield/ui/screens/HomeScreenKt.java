package com.socialshield.ui.screens;

import androidx.compose.animation.*;
import androidx.compose.animation.core.*;
import androidx.compose.foundation.*;
import androidx.compose.foundation.layout.*;
import androidx.compose.foundation.lazy.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.text.font.FontWeight;
import com.socialshield.ui.components.*;
import com.socialshield.ui.theme.*;
import com.socialshield.ui.viewmodel.HomeViewModel;
import com.socialshield.domain.models.ScanType;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000F\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\u001a \u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u0007\u001aB\u0010\u0007\u001a\u00020\u00012\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0007\u001aB\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00010\fH\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u0019\u0010\u001a\u001a\"\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u00052\u0006\u0010\u001d\u001a\u00020\u00052\b\b\u0002\u0010\u0016\u001a\u00020\u0017H\u0007\u001a*\u0010\u001e\u001a\u00020\u00012\u0006\u0010\u001f\u001a\u00020\u00032\u0006\u0010 \u001a\u00020\u00032\u0006\u0010!\u001a\u00020\u00032\b\b\u0002\u0010\u0016\u001a\u00020\u0017H\u0007\u001a\u001c\u0010\"\u001a\u00020\u00012\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a4\u0010#\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00032\u0006\u0010$\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u0017H\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b%\u0010&\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\'"}, d2 = {"HomeHeader", "", "userName", "", "trustScore", "", "totalScans", "HomeScreen", "onScanClick", "Lkotlin/Function1;", "Lcom/socialshield/domain/models/ScanType;", "onHistoryClick", "Lkotlin/Function0;", "onFraudMapClick", "viewModel", "Lcom/socialshield/ui/viewmodel/HomeViewModel;", "QuickActionCard", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "label", "color", "Landroidx/compose/ui/graphics/Color;", "modifier", "Landroidx/compose/ui/Modifier;", "onClick", "QuickActionCard-XO-JAsU", "(Landroidx/compose/ui/graphics/vector/ImageVector;Ljava/lang/String;JLandroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function0;)V", "QuickStatsRow", "fakeDetected", "suspicious", "RecentScanItem", "mediaType", "verdict", "timestamp", "ScanGrid", "StatChip", "value", "StatChip-9LQNqLg", "(Ljava/lang/String;Ljava/lang/String;JLandroidx/compose/ui/Modifier;)V", "app_debug"})
public final class HomeScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void HomeScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.socialshield.domain.models.ScanType, kotlin.Unit> onScanClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onHistoryClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onFraudMapClick, @org.jetbrains.annotations.NotNull()
    com.socialshield.ui.viewmodel.HomeViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void HomeHeader(@org.jetbrains.annotations.NotNull()
    java.lang.String userName, int trustScore, int totalScans) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void QuickStatsRow(int fakeDetected, int suspicious, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ScanGrid(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.socialshield.domain.models.ScanType, kotlin.Unit> onScanClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void RecentScanItem(@org.jetbrains.annotations.NotNull()
    java.lang.String mediaType, @org.jetbrains.annotations.NotNull()
    java.lang.String verdict, @org.jetbrains.annotations.NotNull()
    java.lang.String timestamp, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}
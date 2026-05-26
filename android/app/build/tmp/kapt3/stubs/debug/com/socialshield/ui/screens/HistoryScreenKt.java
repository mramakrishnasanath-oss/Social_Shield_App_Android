package com.socialshield.ui.screens;

import androidx.compose.animation.*;
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
import com.socialshield.ui.theme.*;
import com.socialshield.ui.viewmodel.HistoryViewModel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000&\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a,\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a4\u0010\u0007\u001a\u00020\u00012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\n2\b\b\u0002\u0010\f\u001a\u00020\rH\u0007\u00a8\u0006\u000e"}, d2 = {"HistoryItem", "", "scan", "Lcom/socialshield/domain/models/ScanHistoryItem;", "onClick", "Lkotlin/Function0;", "onDelete", "HistoryScreen", "onBack", "onScanDetail", "Lkotlin/Function1;", "", "viewModel", "Lcom/socialshield/ui/viewmodel/HistoryViewModel;", "app_debug"})
public final class HistoryScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void HistoryScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onScanDetail, @org.jetbrains.annotations.NotNull()
    com.socialshield.ui.viewmodel.HistoryViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void HistoryItem(@org.jetbrains.annotations.NotNull()
    com.socialshield.domain.models.ScanHistoryItem scan, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDelete) {
    }
}
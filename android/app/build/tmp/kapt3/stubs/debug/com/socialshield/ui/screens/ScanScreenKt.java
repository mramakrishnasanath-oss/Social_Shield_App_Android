package com.socialshield.ui.screens;

import android.net.Uri;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.compose.animation.*;
import androidx.compose.foundation.*;
import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import com.socialshield.domain.models.ScanType;
import com.socialshield.ui.components.*;
import com.socialshield.ui.theme.*;
import com.socialshield.ui.viewmodel.ScanViewModel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000J\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\u001aJ\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\fH\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b\r\u0010\u000e\u001a:\u0010\u000f\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\n2\u001e\u0010\u0010\u001a\u001a\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00130\u0012\u0012\u0004\u0012\u00020\u00010\u0011H\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u0014\u0010\u0015\u001a<\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u00182\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u0012\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\u00112\b\b\u0002\u0010\u001b\u001a\u00020\u001cH\u0007\u001a6\u0010\u001d\u001a\u00020\u00012\u0006\u0010\u001e\u001a\u00020\u00072\u0012\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\u00112\u0006\u0010\t\u001a\u00020\nH\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b \u0010!\u001a6\u0010\"\u001a\u00020\u00012\u0006\u0010#\u001a\u00020\u00072\u0012\u0010$\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\u00112\u0006\u0010\t\u001a\u00020\nH\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b%\u0010!\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006&"}, d2 = {"MediaDropZone", "", "selectedUri", "Landroid/net/Uri;", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "label", "", "sublabel", "color", "Landroidx/compose/ui/graphics/Color;", "onPick", "Lkotlin/Function0;", "MediaDropZone-jzV_Hc0", "(Landroid/net/Uri;Landroidx/compose/ui/graphics/vector/ImageVector;Ljava/lang/String;Ljava/lang/String;JLkotlin/jvm/functions/Function0;)V", "ProfileInputSection", "onSubmit", "Lkotlin/Function1;", "", "", "ProfileInputSection-DxMtmZc", "(JLkotlin/jvm/functions/Function1;)V", "ScanScreen", "scanType", "Lcom/socialshield/domain/models/ScanType;", "onBack", "onResultReady", "viewModel", "Lcom/socialshield/ui/viewmodel/ScanViewModel;", "TextInputZone", "text", "onTextChange", "TextInputZone-mxwnekA", "(Ljava/lang/String;Lkotlin/jvm/functions/Function1;J)V", "UrlInputZone", "url", "onUrlChange", "UrlInputZone-mxwnekA", "app_debug"})
public final class ScanScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void ScanScreen(@org.jetbrains.annotations.NotNull()
    com.socialshield.domain.models.ScanType scanType, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onResultReady, @org.jetbrains.annotations.NotNull()
    com.socialshield.ui.viewmodel.ScanViewModel viewModel) {
    }
}
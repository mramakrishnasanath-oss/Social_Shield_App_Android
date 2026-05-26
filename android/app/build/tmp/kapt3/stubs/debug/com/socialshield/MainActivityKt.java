package com.socialshield;

import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.compose.animation.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.*;
import androidx.navigation.NavType;
import androidx.navigation.compose.*;
import com.google.firebase.auth.FirebaseAuth;
import com.socialshield.domain.models.ScanType;
import com.socialshield.ui.screens.*;
import com.socialshield.ui.theme.*;
import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\u001a&\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a\u0010\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\bH\u0007\u00a8\u0006\t"}, d2 = {"ShieldBottomBar", "", "currentRoute", "", "onNavigate", "Lkotlin/Function1;", "SocialShieldApp", "isLoggedIn", "", "app_debug"})
public final class MainActivityKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void SocialShieldApp(boolean isLoggedIn) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ShieldBottomBar(@org.jetbrains.annotations.Nullable()
    java.lang.String currentRoute, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onNavigate) {
    }
}
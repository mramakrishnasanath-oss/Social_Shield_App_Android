package com.socialshield.data.repository;

import android.content.Context;
import android.net.Uri;
import com.google.firebase.auth.FirebaseAuth;
import com.socialshield.data.api.SocialShieldApi;
import com.socialshield.domain.models.ScanResult;
import com.socialshield.domain.models.ScanHistoryItem;
import okhttp3.MultipartBody;
import java.io.File;
import java.io.FileOutputStream;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u000e\u0010\u000f\u001a\u00020\rH\u0082@\u00a2\u0006\u0002\u0010\u0010J&\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00120\n2\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\rH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\b0\n2\u0006\u0010\f\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\nH\u0086@\u00a2\u0006\u0002\u0010\u0010J$\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\b0\n2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0086@\u00a2\u0006\u0002\u0010\u001dJ$\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\b0\n2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0086@\u00a2\u0006\u0002\u0010\u001dJ(\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\b0\n2\u0012\u0010 \u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u00010!H\u0086@\u00a2\u0006\u0002\u0010\"J\u001c\u0010#\u001a\b\u0012\u0004\u0012\u00020\b0\n2\u0006\u0010$\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010%\u001a\b\u0012\u0004\u0012\u00020\b0\n2\u0006\u0010&\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010\u000eJ$\u0010'\u001a\b\u0012\u0004\u0012\u00020\b0\n2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0086@\u00a2\u0006\u0002\u0010\u001dJ(\u0010(\u001a\u00020)2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010*\u001a\u00020\r2\u0006\u0010+\u001a\u00020\rH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"Lcom/socialshield/data/repository/ScanRepository;", "", "api", "Lcom/socialshield/data/api/SocialShieldApi;", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "(Lcom/socialshield/data/api/SocialShieldApi;Lcom/google/firebase/auth/FirebaseAuth;)V", "cachedResult", "Lcom/socialshield/domain/models/ScanResult;", "deleteScan", "Lcom/socialshield/data/repository/ApiResult;", "", "scanId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAuthToken", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getHistory", "", "Lcom/socialshield/domain/models/ScanHistoryItem;", "mediaType", "getScanDetail", "getUserStats", "Lcom/socialshield/data/api/StatsResponse;", "scanAudio", "context", "Landroid/content/Context;", "uri", "Landroid/net/Uri;", "(Landroid/content/Context;Landroid/net/Uri;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "scanImage", "scanProfile", "data", "", "(Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "scanText", "text", "scanUrl", "url", "scanVideo", "uriToFile", "Ljava/io/File;", "prefix", "suffix", "app_debug"})
public final class ScanRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.socialshield.data.api.SocialShieldApi api = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.auth.FirebaseAuth auth = null;
    @org.jetbrains.annotations.Nullable()
    private com.socialshield.domain.models.ScanResult cachedResult;
    
    @javax.inject.Inject()
    public ScanRepository(@org.jetbrains.annotations.NotNull()
    com.socialshield.data.api.SocialShieldApi api, @org.jetbrains.annotations.NotNull()
    com.google.firebase.auth.FirebaseAuth auth) {
        super();
    }
    
    private final java.lang.Object getAuthToken(kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    private final java.io.File uriToFile(android.content.Context context, android.net.Uri uri, java.lang.String prefix, java.lang.String suffix) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object scanImage(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.socialshield.data.repository.ApiResult<com.socialshield.domain.models.ScanResult>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object scanVideo(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.socialshield.data.repository.ApiResult<com.socialshield.domain.models.ScanResult>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object scanAudio(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.socialshield.data.repository.ApiResult<com.socialshield.domain.models.ScanResult>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object scanText(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.socialshield.data.repository.ApiResult<com.socialshield.domain.models.ScanResult>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object scanUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String url, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.socialshield.data.repository.ApiResult<com.socialshield.domain.models.ScanResult>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object scanProfile(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> data, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.socialshield.data.repository.ApiResult<com.socialshield.domain.models.ScanResult>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getHistory(@org.jetbrains.annotations.Nullable()
    java.lang.String mediaType, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.socialshield.data.repository.ApiResult<java.util.List<com.socialshield.domain.models.ScanHistoryItem>>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getScanDetail(@org.jetbrains.annotations.NotNull()
    java.lang.String scanId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.socialshield.data.repository.ApiResult<com.socialshield.domain.models.ScanResult>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteScan(@org.jetbrains.annotations.NotNull()
    java.lang.String scanId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.socialshield.data.repository.ApiResult<kotlin.Unit>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getUserStats(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.socialshield.data.repository.ApiResult<com.socialshield.data.api.StatsResponse>> $completion) {
        return null;
    }
}
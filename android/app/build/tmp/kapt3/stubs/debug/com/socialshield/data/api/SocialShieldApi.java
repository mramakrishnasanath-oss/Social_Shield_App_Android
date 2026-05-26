package com.socialshield.data.api;

import com.socialshield.domain.models.ScanResult;
import com.socialshield.domain.models.ScanHistoryItem;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J(\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\bJ>\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00032\n\b\u0003\u0010\u000b\u001a\u0004\u0018\u00010\u00062\b\b\u0003\u0010\f\u001a\u00020\r2\b\b\u0003\u0010\u000e\u001a\u00020\r2\b\b\u0001\u0010\u0007\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u000fJ(\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u001e\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u00032\b\b\u0001\u0010\u0007\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0014J(\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\b\b\u0001\u0010\u0016\u001a\u00020\u00172\b\b\u0001\u0010\u0007\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0018J(\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\b\b\u0001\u0010\u0016\u001a\u00020\u00172\b\b\u0001\u0010\u0007\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0018J9\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\u0019\b\u0001\u0010\u001b\u001a\u0013\u0012\u0004\u0012\u00020\u0006\u0012\t\u0012\u00070\u0001\u00a2\u0006\u0002\b\u001d0\u001c2\b\b\u0001\u0010\u0007\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u001eJ4\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\u0014\b\u0001\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u001c2\b\b\u0001\u0010\u0007\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u001eJ4\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\u0014\b\u0001\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u001c2\b\b\u0001\u0010\u0007\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u001eJ(\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\b\b\u0001\u0010\u0016\u001a\u00020\u00172\b\b\u0001\u0010\u0007\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0018\u00a8\u0006\""}, d2 = {"Lcom/socialshield/data/api/SocialShieldApi;", "", "deleteScan", "Lretrofit2/Response;", "", "scanId", "", "token", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getHistory", "Lcom/socialshield/data/api/HistoryResponse;", "mediaType", "limit", "", "offset", "(Ljava/lang/String;IILjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getScanDetail", "Lcom/socialshield/domain/models/ScanResult;", "getUserStats", "Lcom/socialshield/data/api/StatsResponse;", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "scanAudio", "file", "Lokhttp3/MultipartBody$Part;", "(Lokhttp3/MultipartBody$Part;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "scanImage", "scanProfile", "body", "", "Lkotlin/jvm/JvmSuppressWildcards;", "(Ljava/util/Map;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "scanText", "scanUrl", "scanVideo", "app_debug"})
public abstract interface SocialShieldApi {
    
    @retrofit2.http.Multipart()
    @retrofit2.http.POST(value = "api/v1/scan/image")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object scanImage(@retrofit2.http.Part()
    @org.jetbrains.annotations.NotNull()
    okhttp3.MultipartBody.Part file, @retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.socialshield.domain.models.ScanResult>> $completion);
    
    @retrofit2.http.Multipart()
    @retrofit2.http.POST(value = "api/v1/scan/video")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object scanVideo(@retrofit2.http.Part()
    @org.jetbrains.annotations.NotNull()
    okhttp3.MultipartBody.Part file, @retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.socialshield.domain.models.ScanResult>> $completion);
    
    @retrofit2.http.Multipart()
    @retrofit2.http.POST(value = "api/v1/scan/audio")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object scanAudio(@retrofit2.http.Part()
    @org.jetbrains.annotations.NotNull()
    okhttp3.MultipartBody.Part file, @retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.socialshield.domain.models.ScanResult>> $completion);
    
    @retrofit2.http.POST(value = "api/v1/scan/text")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object scanText(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> body, @retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.socialshield.domain.models.ScanResult>> $completion);
    
    @retrofit2.http.POST(value = "api/v1/scan/url")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object scanUrl(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> body, @retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.socialshield.domain.models.ScanResult>> $completion);
    
    @retrofit2.http.POST(value = "api/v1/scan/profile")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object scanProfile(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Object> body, @retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.socialshield.domain.models.ScanResult>> $completion);
    
    @retrofit2.http.GET(value = "api/v1/history")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getHistory(@retrofit2.http.Query(value = "media_type")
    @org.jetbrains.annotations.Nullable()
    java.lang.String mediaType, @retrofit2.http.Query(value = "limit")
    int limit, @retrofit2.http.Query(value = "offset")
    int offset, @retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.socialshield.data.api.HistoryResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/v1/history/{scanId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getScanDetail(@retrofit2.http.Path(value = "scanId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String scanId, @retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.socialshield.domain.models.ScanResult>> $completion);
    
    @retrofit2.http.DELETE(value = "api/v1/history/{scanId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteScan(@retrofit2.http.Path(value = "scanId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String scanId, @retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "api/v1/stats")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getUserStats(@retrofit2.http.Header(value = "Authorization")
    @org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.socialshield.data.api.StatsResponse>> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}
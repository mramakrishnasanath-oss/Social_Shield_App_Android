package com.socialshield.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.socialshield.data.api.SocialShieldApi
import com.socialshield.domain.models.ScanResult
import com.socialshield.domain.models.ScanHistoryItem
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

sealed class ApiResult<T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error<T>(val message: String) : ApiResult<T>()
}

@Singleton
class ScanRepository @Inject constructor(
    private val api: SocialShieldApi,
    private val auth: FirebaseAuth
) {
    private var cachedResult: ScanResult? = null


    private suspend fun getAuthToken(): String {
        val user = auth.currentUser ?: return "dev_token"
        return try {
            val token = user.getIdToken(false).await()
            "Bearer ${token.token}"
        } catch (e: Exception) {
            "Bearer dev_token"
        }
    }

    private fun uriToFile(context: Context, uri: Uri, prefix: String, suffix: String): File {
        val file = File.createTempFile(prefix, suffix, context.cacheDir)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        return file
    }

    suspend fun scanImage(context: Context, uri: Uri): ApiResult<ScanResult> = runCatching {
        val token = getAuthToken()
        val file = uriToFile(context, uri, "img_", ".jpg")
        val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
        val response = api.scanImage(part, token)
        file.delete()
        if (response.isSuccessful) {
            val result = response.body()!!
            cachedResult = result
            ApiResult.Success(result)
        } else ApiResult.Error("Server error: ${response.code()}")
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun scanVideo(context: Context, uri: Uri): ApiResult<ScanResult> = runCatching {
        val token = getAuthToken()
        val file = uriToFile(context, uri, "vid_", ".mp4")
        val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("video/*".toMediaTypeOrNull()))
        val response = api.scanVideo(part, token)
        file.delete()
        if (response.isSuccessful) {
            val result = response.body()!!
            cachedResult = result
            ApiResult.Success(result)
        } else ApiResult.Error("Server error: ${response.code()}")
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun scanAudio(context: Context, uri: Uri): ApiResult<ScanResult> = runCatching {
        val token = getAuthToken()
        val file = uriToFile(context, uri, "aud_", ".mp3")
        val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("audio/*".toMediaTypeOrNull()))
        val response = api.scanAudio(part, token)
        file.delete()
        if (response.isSuccessful) {
            val result = response.body()!!
            cachedResult = result
            ApiResult.Success(result)
        } else ApiResult.Error("Server error: ${response.code()}")
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun scanText(text: String): ApiResult<ScanResult> = runCatching {
        val token = getAuthToken()
        val response = api.scanText(mapOf("text" to text), token)
        if (response.isSuccessful) {
            val result = response.body()!!
            cachedResult = result
            ApiResult.Success(result)
        } else ApiResult.Error("Server error: ${response.code()}")
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun scanUrl(url: String): ApiResult<ScanResult> = runCatching {
        val token = getAuthToken()
        val response = api.scanUrl(mapOf("url" to url), token)
        if (response.isSuccessful) {
            val result = response.body()!!
            cachedResult = result
            ApiResult.Success(result)
        } else ApiResult.Error("Server error: ${response.code()}")
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun scanProfile(data: Map<String, Any>): ApiResult<ScanResult> = runCatching {
        val token = getAuthToken()
        val response = api.scanProfile(data, token)
        if (response.isSuccessful) {
            val result = response.body()!!
            cachedResult = result
            ApiResult.Success(result)
        } else ApiResult.Error("Server error: ${response.code()}")
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun getHistory(mediaType: String? = null): ApiResult<List<ScanHistoryItem>> = runCatching {
        val token = getAuthToken()
        val response = api.getHistory(mediaType, token = token)
        if (response.isSuccessful) ApiResult.Success(response.body()!!.items)
        else ApiResult.Error("Server error: ${response.code()}")
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun getScanDetail(scanId: String): ApiResult<ScanResult> = runCatching {
        if (cachedResult?.scanId == scanId) {
            return@runCatching ApiResult.Success(cachedResult!!)
        }
        val token = getAuthToken()
        val response = api.getScanDetail(scanId, token)
        if (response.isSuccessful) ApiResult.Success(response.body()!!)
        else ApiResult.Error("Not found")
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun deleteScan(scanId: String): ApiResult<Unit> = runCatching {
        val token = getAuthToken()
        api.deleteScan(scanId, token)
        ApiResult.Success(Unit)
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun getUserStats() = runCatching {
        val token = getAuthToken()
        val response = api.getUserStats(token)
        if (response.isSuccessful) ApiResult.Success(response.body()!!)
        else ApiResult.Error("Server error")
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }
}

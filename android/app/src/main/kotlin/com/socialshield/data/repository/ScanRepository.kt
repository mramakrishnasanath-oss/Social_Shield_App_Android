package com.socialshield.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.socialshield.data.api.SocialShieldApi
import com.socialshield.data.api.StatsResponse
import com.socialshield.domain.models.ScanResult
import com.socialshield.domain.models.ScanHistoryItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
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

    private suspend fun saveScanToFirestore(result: ScanResult, profileData: Map<String, Any>? = null): ScanResult {
        val userId = auth.currentUser?.uid ?: return result
        
        // 1. Override verdict/risk based on the risk score logic:
        // Risk Score < 30 -> Safe
        // Risk Score 30-70 -> Suspicious
        // Risk Score > 70 -> Fake
        val score = result.fakeProbability
        val verdict = when {
            score < 30f -> "SAFE"
            score <= 70f -> "SUSPICIOUS"
            else -> "FAKE"
        }
        val riskLevel = when (verdict) {
            "SAFE" -> "LOW"
            "SUSPICIOUS" -> "MEDIUM"
            else -> "HIGH"
        }
        
        // Dynamic client-side recommendations generation
        val recommendations = if (result.recommendations.isNotEmpty()) result.recommendations else {
            when (verdict) {
                "FAKE" -> listOf(
                    "Block and report this profile to the platform administration.",
                    "Do not engage or click on any links in the bio or messages.",
                    "Warn your contacts about possible scam impersonation from this account."
                )
                "SUSPICIOUS" -> listOf(
                    "Exercise caution when communicating or sharing information.",
                    "Do not click on external links in their bio or posts.",
                    "Verify their identity through a secondary, trusted channel."
                )
                else -> listOf(
                    "This profile appears safe, but continue to practice general safety.",
                    "Avoid sharing personal details or financial info unless verified.",
                    "Ensure your own social security and two-factor authentication are enabled."
                )
            }
        }
        
        val updatedResult = result.copy(
            verdict = verdict,
            riskLevel = riskLevel,
            recommendations = recommendations
        )
        
        val scanDocRef = firestore.collection("users").document(userId)
            .collection("scans").document(updatedResult.scanId)
            
        val scanMap = mutableMapOf<String, Any>(
            "scanId" to updatedResult.scanId,
            "userId" to userId,
            "mediaType" to updatedResult.mediaType,
            "verdict" to updatedResult.verdict,
            "confidence" to updatedResult.confidence,
            "fakeProbability" to updatedResult.fakeProbability,
            "realProbability" to updatedResult.realProbability,
            "riskLevel" to updatedResult.riskLevel,
            "explanations" to updatedResult.explanations,
            "recommendations" to updatedResult.recommendations,
            "timestamp" to updatedResult.timestamp
        )
        updatedResult.heatmapBase64?.let { scanMap["heatmapBase64"] = it }
        
        if (profileData != null) {
            scanMap["username"] = profileData["username"] ?: ""
            scanMap["profileName"] = profileData["profileName"] ?: profileData["username"] ?: "Unknown User"
            scanMap["platform"] = profileData["platform"] ?: "Instagram"
        } else if (updatedResult.mediaType == "PROFILE" && updatedResult.metadata != null) {
            scanMap["username"] = updatedResult.metadata["username"]?.toString() ?: ""
            scanMap["profileName"] = updatedResult.metadata["profileName"]?.toString() ?: updatedResult.metadata["username"]?.toString() ?: "Unknown User"
            scanMap["platform"] = updatedResult.metadata["platform"]?.toString() ?: "Instagram"
        } else {
            scanMap["username"] = ""
            scanMap["profileName"] = updatedResult.mediaType
            scanMap["platform"] = ""
        }

        scanDocRef.set(scanMap).await()
        
        // 2. Update stats document transactionally
        val userDocRef = firestore.collection("users").document(userId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userDocRef)
            var totalScans = snapshot.getLong("totalScans") ?: 0L
            var fakeDetected = snapshot.getLong("fakeDetected") ?: 0L
            var suspiciousDetected = snapshot.getLong("suspiciousDetected") ?: 0L
            var safeDetected = snapshot.getLong("safeDetected") ?: 0L
            
            totalScans += 1
            when (verdict) {
                "SAFE" -> safeDetected += 1
                "SUSPICIOUS" -> suspiciousDetected += 1
                "FAKE" -> fakeDetected += 1
            }
            
            val trustScore = if (totalScans > 0) {
                ((safeDetected.toFloat() + 0.5f * suspiciousDetected.toFloat()) / totalScans.toFloat() * 100f).toInt()
            } else 100
            
            transaction.set(userDocRef, mapOf(
                "totalScans" to totalScans,
                "fakeDetected" to fakeDetected,
                "suspiciousDetected" to suspiciousDetected,
                "safeDetected" to safeDetected,
                "trustScore" to trustScore
            ))
        }.await()
        
        return updatedResult
    }

    fun getUserStatsFlow(): Flow<StatsResponse?> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val docRef = firestore.collection("users").document(userId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val total = snapshot.getLong("totalScans")?.toInt() ?: 0
                val fake = snapshot.getLong("fakeDetected")?.toInt() ?: 0
                val suspicious = snapshot.getLong("suspiciousDetected")?.toInt() ?: 0
                val trust = snapshot.getLong("trustScore")?.toInt() ?: 100
                trySend(StatsResponse(total, fake, suspicious, trust))
            } else {
                trySend(StatsResponse(0, 0, 0, 100))
            }
        }
        awaitClose { listener.remove() }
    }

    fun getHistoryFlow(mediaType: String? = null): Flow<List<ScanHistoryItem>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        var query = firestore.collection("users").document(userId)
            .collection("scans")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            
        if (mediaType != null) {
            query = query.whereEqualTo("mediaType", mediaType)
        }
        
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = mutableListOf<ScanHistoryItem>()
            if (snapshot != null) {
                for (doc in snapshot.documents) {
                    val scanId = doc.getString("scanId") ?: ""
                    val mType = doc.getString("mediaType") ?: ""
                    val verdict = doc.getString("verdict") ?: ""
                    val confidence = doc.getDouble("confidence")?.toFloat() ?: 0f
                    val timestamp = doc.getString("timestamp") ?: ""
                    list.add(ScanHistoryItem(scanId, mType, verdict, confidence, timestamp))
                }
            }
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun scanImage(context: Context, uri: Uri): ApiResult<ScanResult> = runCatching {
        val token = getAuthToken()
        val file = uriToFile(context, uri, "img_", ".jpg")
        val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
        val response = api.scanImage(part, token)
        file.delete()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            val savedResult = saveScanToFirestore(body)
            cachedResult = savedResult
            ApiResult.Success(savedResult)
        } else {
            ApiResult.Error("Server error: ${response.code()} or empty response")
        }
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun scanVideo(context: Context, uri: Uri): ApiResult<ScanResult> = runCatching {
        val token = getAuthToken()
        val file = uriToFile(context, uri, "vid_", ".mp4")
        val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("video/*".toMediaTypeOrNull()))
        val response = api.scanVideo(part, token)
        file.delete()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            val savedResult = saveScanToFirestore(body)
            cachedResult = savedResult
            ApiResult.Success(savedResult)
        } else {
            ApiResult.Error("Server error: ${response.code()} or empty response")
        }
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun scanAudio(context: Context, uri: Uri): ApiResult<ScanResult> = runCatching {
        val token = getAuthToken()
        val file = uriToFile(context, uri, "aud_", ".mp3")
        val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("audio/*".toMediaTypeOrNull()))
        val response = api.scanAudio(part, token)
        file.delete()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            val savedResult = saveScanToFirestore(body)
            cachedResult = savedResult
            ApiResult.Success(savedResult)
        } else {
            ApiResult.Error("Server error: ${response.code()} or empty response")
        }
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun scanText(text: String): ApiResult<ScanResult> = runCatching {
        val token = getAuthToken()
        val response = api.scanText(mapOf("text" to text), token)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            val savedResult = saveScanToFirestore(body)
            cachedResult = savedResult
            ApiResult.Success(savedResult)
        } else {
            ApiResult.Error("Server error: ${response.code()} or empty response")
        }
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun scanUrl(url: String): ApiResult<ScanResult> = runCatching {
        val token = getAuthToken()
        val response = api.scanUrl(mapOf("url" to url), token)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            val savedResult = saveScanToFirestore(body)
            cachedResult = savedResult
            ApiResult.Success(savedResult)
        } else {
            ApiResult.Error("Server error: ${response.code()} or empty response")
        }
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun scanProfile(data: Map<String, Any>): ApiResult<ScanResult> = runCatching {
        val token = getAuthToken()
        val response = api.scanProfile(data, token)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            val savedResult = saveScanToFirestore(body, data)
            cachedResult = savedResult
            ApiResult.Success(savedResult)
        } else {
            ApiResult.Error("Server error: ${response.code()} or empty response")
        }
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun getHistory(mediaType: String? = null): ApiResult<List<ScanHistoryItem>> = runCatching {
        val token = getAuthToken()
        val response = api.getHistory(mediaType, token = token)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            ApiResult.Success(body.items)
        } else {
            ApiResult.Error("Server error: ${response.code()} or empty response")
        }
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun getScanDetail(scanId: String): ApiResult<ScanResult> = runCatching {
        val userId = auth.currentUser?.uid ?: return ApiResult.Error("Not authenticated")
        val snapshot = firestore.collection("users").document(userId)
            .collection("scans").document(scanId).get().await()
        if (snapshot.exists()) {
            val explanationsList = (snapshot.get("explanations") as? List<*>)?.map { it.toString() } ?: emptyList()
            val recommendationsList = (snapshot.get("recommendations") as? List<*>)?.map { it.toString() } ?: emptyList()
            val result = ScanResult(
                scanId = snapshot.getString("scanId") ?: "",
                userId = snapshot.getString("userId") ?: "",
                mediaType = snapshot.getString("mediaType") ?: "",
                verdict = snapshot.getString("verdict") ?: "",
                confidence = snapshot.getDouble("confidence")?.toFloat() ?: 0f,
                fakeProbability = snapshot.getDouble("fakeProbability")?.toFloat() ?: 0f,
                realProbability = snapshot.getDouble("realProbability")?.toFloat() ?: 0f,
                riskLevel = snapshot.getString("riskLevel") ?: "",
                explanations = explanationsList,
                recommendations = recommendationsList,
                heatmapBase64 = snapshot.getString("heatmapBase64"),
                timestamp = snapshot.getString("timestamp") ?: ""
            )
            ApiResult.Success(result)
        } else {
            if (cachedResult?.scanId == scanId) {
                return@runCatching ApiResult.Success(cachedResult!!)
            }
            val token = getAuthToken()
            val response = api.getScanDetail(scanId, token)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.Error("Scan details not found")
            }
        }
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun deleteScan(scanId: String): ApiResult<Unit> = runCatching {
        val userId = auth.currentUser?.uid ?: return ApiResult.Error("Not authenticated")
        val scanDocRef = firestore.collection("users").document(userId)
            .collection("scans").document(scanId)
        val snapshot = scanDocRef.get().await()
        if (snapshot.exists()) {
            val verdict = snapshot.getString("verdict") ?: ""
            scanDocRef.delete().await()
            val userDocRef = firestore.collection("users").document(userId)
            firestore.runTransaction { transaction ->
                val userSnap = transaction.get(userDocRef)
                var totalScans = userSnap.getLong("totalScans") ?: 0L
                var fakeDetected = userSnap.getLong("fakeDetected") ?: 0L
                var suspiciousDetected = userSnap.getLong("suspiciousDetected") ?: 0L
                var safeDetected = userSnap.getLong("safeDetected") ?: 0L
                if (totalScans > 0) totalScans -= 1
                when (verdict) {
                    "SAFE" -> if (safeDetected > 0) safeDetected -= 1
                    "SUSPICIOUS" -> if (suspiciousDetected > 0) suspiciousDetected -= 1
                    "FAKE" -> if (fakeDetected > 0) fakeDetected -= 1
                }
                val trustScore = if (totalScans > 0) {
                    ((safeDetected.toFloat() + 0.5f * suspiciousDetected.toFloat()) / totalScans.toFloat() * 100f).toInt()
                } else 100
                transaction.set(userDocRef, mapOf(
                    "totalScans" to totalScans,
                    "fakeDetected" to fakeDetected,
                    "suspiciousDetected" to suspiciousDetected,
                    "safeDetected" to safeDetected,
                    "trustScore" to trustScore
                ))
            }.await()
        }
        val token = getAuthToken()
        api.deleteScan(scanId, token)
        ApiResult.Success(Unit)
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }

    suspend fun getUserStats() = runCatching {
        val token = getAuthToken()
        val response = api.getUserStats(token)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            ApiResult.Success(body)
        } else {
            ApiResult.Error("Server error or empty stats")
        }
    }.getOrElse { ApiResult.Error(it.message ?: "Unknown error") }
}


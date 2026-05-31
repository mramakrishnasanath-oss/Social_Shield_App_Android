// ─── Auth ViewModel ──────────────────────────────────────────────────────────
package com.socialshield.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.socialshield.data.repository.ApiResult
import com.socialshield.data.repository.ScanRepository
import com.socialshield.domain.models.ScanHistoryItem
import com.socialshield.domain.models.ScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── Auth ─────────────────────────────────────────────────────────────────────
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(private val auth: FirebaseAuth) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState(isLoggedIn = auth.currentUser != null))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Email and password required") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { _uiState.update { it.copy(isLoading = false, isLoggedIn = true) } }
            .addOnFailureListener { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
    }

    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Email and password required") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { _uiState.update { it.copy(isLoading = false, isLoggedIn = true) } }
            .addOnFailureListener { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
    }

    fun signInWithGoogle() {
        // Google sign-in flow handled in Activity - here we just update state after credential
        _uiState.update { it.copy(error = "Use the Google Sign-In activity flow") }
    }

    fun signOut() {
        auth.signOut()
        _uiState.update { AuthUiState(isLoggedIn = false) }
    }
}

// ─── Home ─────────────────────────────────────────────────────────────────────
data class HomeUiState(
    val userName: String = "",
    val trustScore: Int = 100,
    val totalScans: Int = 0,
    val fakeDetected: Int = 0,
    val suspiciousDetected: Int = 0,
    val recentScans: List<ScanHistoryItem> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: ScanRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val user = auth.currentUser
        _uiState.update { it.copy(userName = user?.displayName ?: user?.email?.substringBefore('@') ?: "") }
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        when (val statsResult = repo.getUserStats()) {
            is ApiResult.Success -> _uiState.update {
                it.copy(
                    trustScore = statsResult.data.trustScore,
                    totalScans = statsResult.data.totalScans,
                    fakeDetected = statsResult.data.fakeDetected,
                    suspiciousDetected = statsResult.data.suspiciousDetected
                )
            }
            else -> {}
        }
        when (val historyResult = repo.getHistory()) {
            is ApiResult.Success -> _uiState.update { it.copy(recentScans = historyResult.data) }
            else -> {}
        }
    }
}

// ─── Scan ─────────────────────────────────────────────────────────────────────
data class ScanUiState(
    val isScanning: Boolean = false,
    val scanId: String? = null,
    val error: String? = null
)

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repo: ScanRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun scanImage(ctx: Context, uri: Uri) = scan { repo.scanImage(ctx, uri) }
    fun scanVideo(ctx: Context, uri: Uri) = scan { repo.scanVideo(ctx, uri) }
    fun scanAudio(ctx: Context, uri: Uri) = scan { repo.scanAudio(ctx, uri) }
    fun scanText(text: String) = scan { repo.scanText(text) }
    fun scanUrl(url: String) = scan { repo.scanUrl(url) }
    fun scanProfile(data: Map<String, Any>) = scan { repo.scanProfile(data) }

    private fun scan(block: suspend () -> ApiResult<ScanResult>) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.update { it.copy(isScanning = true, error = null, scanId = null) }
        when (val result = block()) {
            is ApiResult.Success -> _uiState.update { it.copy(isScanning = false, scanId = result.data.scanId) }
            is ApiResult.Error -> _uiState.update { it.copy(isScanning = false, error = result.message) }
        }
    }
}

// ─── Result ───────────────────────────────────────────────────────────────────
@HiltViewModel
class ResultViewModel @Inject constructor(private val repo: ScanRepository) : ViewModel() {
    private val _result = MutableStateFlow<ScanResult?>(null)
    val result: StateFlow<ScanResult?> = _result.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadResult(scanId: String) = viewModelScope.launch {
        _error.value = null
        when (val r = repo.getScanDetail(scanId)) {
            is ApiResult.Success -> _result.value = r.data
            is ApiResult.Error -> _error.value = r.message
        }
    }
}

// ─── History ─────────────────────────────────────────────────────────────────
data class HistoryUiState(
    val isLoading: Boolean = false,
    val scans: List<ScanHistoryItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(private val repo: ScanRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun loadHistory(mediaType: String? = null) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        when (val result = repo.getHistory(mediaType)) {
            is ApiResult.Success -> _uiState.update { it.copy(isLoading = false, scans = result.data) }
            is ApiResult.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
        }
    }

    fun deleteScan(scanId: String) = viewModelScope.launch {
        repo.deleteScan(scanId)
        _uiState.update { it.copy(scans = it.scans.filter { s -> s.scanId != scanId }) }
    }
}

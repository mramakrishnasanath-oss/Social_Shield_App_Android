package com.socialshield.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.socialshield.domain.models.ScanType
import com.socialshield.ui.components.*
import com.socialshield.ui.theme.*
import com.socialshield.ui.viewmodel.ScanViewModel

@Composable
fun ScanScreen(
    scanType: ScanType,
    onBack: () -> Unit,
    onResultReady: (String) -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var textInput by remember { mutableStateOf("") }
    var urlInput by remember { mutableStateOf("") }

    LaunchedEffect(uiState.scanId) {
        uiState.scanId?.let { onResultReady(it) }
    }

    // File pickers
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { selectedUri = it }
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { selectedUri = it }
    }
    val audioPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { selectedUri = it }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepBlack, DarkSurface)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color(0xFF0F172A), modifier = Modifier.size(20.dp))
                }
                Text(
                    scanType.displayName,
                    color = Color(0xFF0F172A), fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Upload zone / input area
                when (scanType) {
                    ScanType.IMAGE -> {
                        MediaDropZone(
                            selectedUri = selectedUri,
                            icon = Icons.Default.Image,
                            label = "Upload an Image",
                            sublabel = "JPG, PNG, WEBP — max 20MB",
                            color = NeonBlue,
                            onPick = { imagePicker.launch("image/*") }
                        )
                    }
                    ScanType.VIDEO -> {
                        MediaDropZone(
                            selectedUri = selectedUri,
                            icon = Icons.Default.VideoFile,
                            label = "Upload a Video",
                            sublabel = "MP4, MOV, AVI — max 500MB",
                            color = NeonPurple,
                            onPick = { videoPicker.launch("video/*") }
                        )
                    }
                    ScanType.AUDIO -> {
                        MediaDropZone(
                            selectedUri = selectedUri,
                            icon = Icons.Default.Mic,
                            label = "Upload Audio",
                            sublabel = "MP3, WAV, M4A — max 100MB",
                            color = NeonCyan,
                            onPick = { audioPicker.launch("audio/*") }
                        )
                    }
                    ScanType.TEXT -> {
                        TextInputZone(
                            text = textInput,
                            onTextChange = { textInput = it },
                            color = NeonPink
                        )
                    }
                    ScanType.URL -> {
                        UrlInputZone(
                            url = urlInput,
                            onUrlChange = { urlInput = it },
                            color = Color(0xFFFFB800)
                        )
                    }
                    ScanType.PROFILE -> {
                        ProfileInputSection(
                            color = Color(0xFF00E5FF),
                            onSubmit = { profileData -> viewModel.scanProfile(profileData) }
                        )
                    }
                }

                // Error
                uiState.error?.let {
                    Text(it, color = RiskHigh, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }

                // Scan button
                if (scanType != ScanType.PROFILE) {
                    val isReady = when (scanType) {
                        ScanType.IMAGE, ScanType.VIDEO, ScanType.AUDIO -> selectedUri != null
                        ScanType.TEXT -> textInput.isNotBlank()
                        ScanType.URL -> urlInput.isNotBlank()
                        else -> false
                    }
                    val color = when (scanType) {
                        ScanType.IMAGE -> NeonBlue; ScanType.VIDEO -> NeonPurple; ScanType.AUDIO -> NeonCyan
                        ScanType.TEXT -> NeonPink; ScanType.URL -> Color(0xFFFFB800); else -> NeonBlue
                    }
                    NeonButton(
                        text = "Analyze with AI",
                        onClick = {
                            when (scanType) {
                                ScanType.IMAGE -> selectedUri?.let { viewModel.scanImage(context, it) }
                                ScanType.VIDEO -> selectedUri?.let { viewModel.scanVideo(context, it) }
                                ScanType.AUDIO -> selectedUri?.let { viewModel.scanAudio(context, it) }
                                ScanType.TEXT -> viewModel.scanText(textInput)
                                ScanType.URL -> viewModel.scanUrl(urlInput)
                                else -> {}
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = color,
                        enabled = isReady,
                        isLoading = uiState.isScanning
                    )
                }

                // Info card
                GlassCard {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Info, null, tint = NeonBlue.copy(0.7f), modifier = Modifier.size(18.dp))
                        Text(
                            scanType.infoText,
                            color = Color(0xFF0F172A).copy(0.65f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediaDropZone(
    selectedUri: Uri?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    sublabel: String,
    color: Color,
    onPick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(0.06f))
            .border(
                2.dp,
                Brush.linearGradient(listOf(color.copy(0.6f), color.copy(0.1f))),
                RoundedCornerShape(20.dp)
            )
            .clickable { onPick() },
        contentAlignment = Alignment.Center
    ) {
        if (selectedUri != null) {
            AsyncImage(
                model = selectedUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))
            )
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, null, tint = RiskLow, modifier = Modifier.size(36.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("File selected — tap to change", color = Color(0xFF0F172A), fontSize = 13.sp)
                }
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(color.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(36.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text(label, color = Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(sublabel, color = Color(0xFF0F172A).copy(0.5f), fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Text("Tap to browse files", color = color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun TextInputZone(text: String, onTextChange: (String) -> Unit, color: Color) {
    Column {
        Text("Paste Text to Analyze", color = Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth().height(200.dp),
            placeholder = { Text("Paste suspicious message, email content, or any text here...", color = Color(0xFF0F172A).copy(0.35f), fontSize = 13.sp) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color,
                unfocusedBorderColor = GlassBorder,
                focusedTextColor = Color(0xFF0F172A),
                unfocusedTextColor = Color(0xFF0F172A),
                cursorColor = color,
                focusedContainerColor = GlassWhite,
                unfocusedContainerColor = GlassWhite
            )
        )
        Text("${text.length} / 10,000 characters", color = Color(0xFF0F172A).copy(0.4f), fontSize = 11.sp, modifier = Modifier.align(Alignment.End).padding(top = 4.dp))
    }
}

@Composable
fun UrlInputZone(url: String, onUrlChange: (String) -> Unit, color: Color) {
    Column {
        Text("Enter URL to Check", color = Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("https://suspicious-link.com/verify", color = Color(0xFF0F172A).copy(0.35f), fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Link, null, tint = color.copy(0.7f), modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color,
                unfocusedBorderColor = GlassBorder,
                focusedTextColor = Color(0xFF0F172A),
                unfocusedTextColor = Color(0xFF0F172A),
                cursorColor = color,
                focusedContainerColor = GlassWhite,
                unfocusedContainerColor = GlassWhite
            ),
            singleLine = true
        )
    }
}

@Composable
fun ProfileInputSection(color: Color, onSubmit: (Map<String, Any>) -> Unit) {
    var username by remember { mutableStateOf("") }
    var followers by remember { mutableStateOf("") }
    var following by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var accountAge by remember { mutableStateOf("") }
    var posts by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Profile Details", color = Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

        listOf(
            Triple("Username", username) { v: String -> username = v },
            Triple("Followers", followers) { v: String -> followers = v },
            Triple("Following", following) { v: String -> following = v },
            Triple("Account Age (days)", accountAge) { v: String -> accountAge = v },
            Triple("Post Count", posts) { v: String -> posts = v }
        ).forEach { (label, value, setter) ->
            OutlinedTextField(
                value = value,
                onValueChange = setter,
                label = { Text(label, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = color, unfocusedBorderColor = GlassBorder,
                    focusedLabelColor = color, focusedTextColor = Color(0xFF0F172A), unfocusedTextColor = Color(0xFF0F172A),
                    focusedContainerColor = GlassWhite, unfocusedContainerColor = GlassWhite
                )
            )
        }
        OutlinedTextField(
            value = bio, onValueChange = { bio = it },
            label = { Text("Bio", fontSize = 13.sp) },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color, unfocusedBorderColor = GlassBorder,
                focusedTextColor = Color(0xFF0F172A), unfocusedTextColor = Color(0xFF0F172A),
                focusedContainerColor = GlassWhite, unfocusedContainerColor = GlassWhite
            )
        )

        NeonButton(
            text = "Analyze Profile",
            onClick = {
                onSubmit(mapOf(
                    "username" to username,
                    "followers" to (followers.toIntOrNull() ?: 0),
                    "following" to (following.toIntOrNull() ?: 0),
                    "bio" to bio,
                    "account_age_days" to (accountAge.toIntOrNull() ?: 0),
                    "post_count" to (posts.toIntOrNull() ?: 0)
                ))
            },
            modifier = Modifier.fillMaxWidth(),
            color = color
        )
    }
}

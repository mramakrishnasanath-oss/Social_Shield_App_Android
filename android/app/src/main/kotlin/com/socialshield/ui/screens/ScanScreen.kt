package com.socialshield.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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

    // Bypassed navigation so that the result is shown inline on this screen.
    // LaunchedEffect(uiState.scanId) {
    //     uiState.scanId?.let { onResultReady(it) }
    // }

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
                    onClick = {
                        viewModel.resetScan()
                        onBack()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = ContentColor, modifier = Modifier.size(20.dp))
                }
                Text(
                    scanType.displayName,
                    color = ContentColor, fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
            }

            val result = uiState.scanResult
            if (result != null) {
                var showHeatmap by remember { mutableStateOf(false) }
                val verdictColor = when (result.verdict) {
                    "FAKE" -> RiskHigh; "SUSPICIOUS" -> RiskMedium; else -> RiskLow
                }
                val verdictBg = when (result.verdict) {
                    "FAKE" -> RiskHigh.copy(0.08f); "SUSPICIOUS" -> RiskMedium.copy(0.08f); else -> RiskLow.copy(0.08f)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Big verdict section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(verdictBg)
                            .border(1.dp, verdictColor.copy(0.3f), RoundedCornerShape(20.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(verdictColor.copy(0.15f))
                                    .border(2.dp, verdictColor.copy(0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    when (result.verdict) {
                                        "FAKE" -> Icons.Default.GppBad
                                        "SUSPICIOUS" -> Icons.Default.GppMaybe
                                        else -> Icons.Default.GppGood
                                    },
                                    null,
                                    tint = verdictColor,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                result.verdict,
                                color = verdictColor,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            ConfidenceArc(percentage = result.confidence, verdict = result.verdict)
                        }
                    }

                    // Risk level
                    GlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Risk Level", color = ContentColor.copy(0.6f), fontSize = 12.sp)
                                Spacer(Modifier.height(8.dp))
                                RiskIndicator(result.riskLevel)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Fake Probability", color = ContentColor.copy(0.6f), fontSize = 12.sp)
                                Text(
                                    "${result.fakeProbability.toInt()}%",
                                    color = verdictColor,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Probability analysis
                    GlassCard {
                        Text("Probability Analysis", color = ContentColor, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(16.dp))
                        ProbabilityBar(label = "Fake", value = result.fakeProbability, color = RiskHigh)
                        Spacer(Modifier.height(8.dp))
                        ProbabilityBar(label = "Real", value = result.realProbability, color = RiskLow)
                    }

                    // Heatmap (if available)
                    result.heatmapBase64?.let { heatmapB64 ->
                        GlassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Manipulation Heatmap", color = ContentColor, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Red areas indicate manipulation", color = ContentColor.copy(0.5f), fontSize = 12.sp)
                                }
                                TextButton(onClick = { showHeatmap = !showHeatmap }) {
                                    Text(if (showHeatmap) "Hide" else "Show", color = NeonBlue, fontSize = 13.sp)
                                }
                            }
                            AnimatedVisibility(showHeatmap) {
                                val bitmap = remember(heatmapB64) {
                                    try {
                                        val bytes = Base64.decode(heatmapB64, Base64.DEFAULT)
                                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                                    } catch (e: Exception) { null }
                                }
                                bitmap?.let {
                                    Spacer(Modifier.height(12.dp))
                                    androidx.compose.foundation.Image(
                                        bitmap = it,
                                        contentDescription = "Manipulation heatmap",
                                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.FillWidth
                                    )
                                }
                            }
                        }
                    }

                    // AI Explanation
                    GlassCard {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Psychology, null, tint = NeonBlue, modifier = Modifier.size(20.dp))
                            Text("AI Explanation", color = ContentColor, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(14.dp))
                        result.explanations.forEach { explanation ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier.size(6.dp).offset(y = 7.dp)
                                        .clip(CircleShape).background(verdictColor)
                                )
                                Text(
                                    explanation,
                                    color = ContentColor.copy(0.85f),
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    // Recommendations
                    GlassCard {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Security, null, tint = RiskLow, modifier = Modifier.size(20.dp))
                            Text("Safety Recommendations", color = ContentColor, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(14.dp))
                        result.recommendations.forEach { rec ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = RiskLow, modifier = Modifier.size(16.dp).offset(y = 2.dp))
                                Text(
                                    rec,
                                    color = ContentColor.copy(0.85f),
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.resetScan()
                                onBack()
                            },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            border = BorderStroke(1.dp, GlassBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ContentColor)
                        ) {
                            Text("← Exit")
                        }
                        NeonButton(
                            text = "Scan Again",
                            onClick = {
                                selectedUri = null
                                textInput = ""
                                urlInput = ""
                                viewModel.resetScan()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else if (uiState.isScanning) {
                var currentStep by remember { mutableStateOf(0) }
                var completedSteps by remember { mutableStateOf(setOf<Int>()) }

                LaunchedEffect(uiState.isScanning) {
                    if (uiState.isScanning) {
                        currentStep = 0
                        completedSteps = emptySet()
                        val steps = listOf(
                            "Initializing AI neural network…",
                            "Preprocessing input data…",
                            "Analyzing patterns & features…",
                            "Running deepfake detection model…",
                            "Detecting visual inconsistencies…",
                            "Generating Grad-CAM heatmap…",
                            "Computing confidence score…",
                            "Compiling AI explanation…",
                            "Finalizing threat assessment…"
                        )
                        steps.indices.forEach { i ->
                            kotlinx.coroutines.delay(600L)
                            completedSteps = completedSteps + (i - 1)
                            currentStep = i
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ScanningPulse(color = NeonBlue, modifier = Modifier.fillMaxSize())
                        Text(
                            "AI",
                            color = ContentColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Analyzing content…",
                        color = ContentColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(32.dp))

                    val steps = listOf(
                        "Initializing AI neural network…",
                        "Preprocessing input data…",
                        "Analyzing patterns & features…",
                        "Running deepfake detection model…",
                        "Detecting visual inconsistencies…",
                        "Generating Grad-CAM heatmap…",
                        "Computing confidence score…",
                        "Compiling AI explanation…",
                        "Finalizing threat assessment…"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(GlassWhite)
                            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        steps.take(currentStep + 1).forEachIndexed { i, step ->
                            val isDone = i in completedSteps
                            val isCurrent = i == currentStep

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (isDone) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        null,
                                        tint = RiskLow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (isCurrent) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = NeonBlue,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(GlassWhite)
                                            .border(1.dp, GlassBorder, CircleShape)
                                    )
                                }

                                Text(
                                    step,
                                    color = when {
                                        isDone -> ContentColor.copy(0.4f)
                                        isCurrent -> ContentColor
                                        else -> ContentColor.copy(0.2f)
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            } else {
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
                                color = ContentColor.copy(0.65f),
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
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
                    Text("File selected — tap to change", color = ContentColor, fontSize = 13.sp)
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
                Text(label, color = ContentColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(sublabel, color = ContentColor.copy(0.5f), fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Text("Tap to browse files", color = color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun TextInputZone(text: String, onTextChange: (String) -> Unit, color: Color) {
    Column {
        Text("Paste Text to Analyze", color = ContentColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth().height(200.dp),
            placeholder = { Text("Paste suspicious message, email content, or any text here...", color = ContentColor.copy(0.35f), fontSize = 13.sp) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color,
                unfocusedBorderColor = GlassBorder,
                focusedTextColor = ContentColor,
                unfocusedTextColor = ContentColor,
                cursorColor = color,
                focusedContainerColor = GlassWhite,
                unfocusedContainerColor = GlassWhite
            )
        )
        Text("${text.length} / 10,000 characters", color = ContentColor.copy(0.4f), fontSize = 11.sp, modifier = Modifier.align(Alignment.End).padding(top = 4.dp))
    }
}

@Composable
fun UrlInputZone(url: String, onUrlChange: (String) -> Unit, color: Color) {
    Column {
        Text("Enter URL to Check", color = ContentColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("https://suspicious-link.com/verify", color = ContentColor.copy(0.35f), fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Link, null, tint = color.copy(0.7f), modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color,
                unfocusedBorderColor = GlassBorder,
                focusedTextColor = ContentColor,
                unfocusedTextColor = ContentColor,
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
    var likesPerPost by remember { mutableStateOf("") }
    var commentsPerPost by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Profile Details", color = ContentColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

        listOf(
            Triple("Username", username) { v: String -> username = v },
            Triple("Followers", followers) { v: String -> followers = v },
            Triple("Following", following) { v: String -> following = v },
            Triple("Account Age (days)", accountAge) { v: String -> accountAge = v },
            Triple("Post Count", posts) { v: String -> posts = v },
            Triple("Average Likes per Post", likesPerPost) { v: String -> likesPerPost = v },
            Triple("Average Comments per Post", commentsPerPost) { v: String -> commentsPerPost = v }
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
                    focusedLabelColor = color, focusedTextColor = ContentColor, unfocusedTextColor = ContentColor,
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
                focusedTextColor = ContentColor, unfocusedTextColor = ContentColor,
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
                    "post_count" to (posts.toIntOrNull() ?: 0),
                    "likes_per_post" to (likesPerPost.toIntOrNull() ?: 0),
                    "comments_per_post" to (commentsPerPost.toIntOrNull() ?: 0)
                ))
            },
            modifier = Modifier.fillMaxWidth(),
            color = color
        )
    }
}


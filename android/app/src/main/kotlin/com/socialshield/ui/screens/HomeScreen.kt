package com.socialshield.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.socialshield.ui.components.*
import com.socialshield.ui.theme.*
import com.socialshield.ui.viewmodel.HomeViewModel
import com.socialshield.domain.models.ScanType

@Composable
fun HomeScreen(
    onScanClick: (ScanType) -> Unit,
    onHistoryClick: () -> Unit,
    onFraudMapClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepBlack, DarkSurface))),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            HomeHeader(
                userName = uiState.userName,
                trustScore = uiState.trustScore,
                totalScans = uiState.totalScans
            )
        }

        // Quick stats row
        item {
            QuickStatsRow(
                fakeDetected = uiState.fakeDetected,
                suspicious = uiState.suspiciousDetected,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(24.dp))
        }

        // Scan type cards
        item {
            Text(
                "Scan & Detect",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))
        }

        item {
            ScanGrid(onScanClick = onScanClick)
            Spacer(Modifier.height(24.dp))
        }

        // Quick actions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    icon = Icons.Default.History,
                    label = "Scan History",
                    color = NeonPurple,
                    modifier = Modifier.weight(1f),
                    onClick = onHistoryClick
                )
                QuickActionCard(
                    icon = Icons.Default.Public,
                    label = "Fraud Map",
                    color = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = onFraudMapClick
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        // Recent scans
        if (uiState.recentScans.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Scans", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onHistoryClick) {
                        Text("View All", color = NeonBlue, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            items(uiState.recentScans.take(4)) { scan ->
                RecentScanItem(
                    mediaType = scan.mediaType,
                    verdict = scan.verdict,
                    confidence = scan.confidence,
                    timestamp = scan.timestamp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun HomeHeader(userName: String, trustScore: Int, totalScans: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(NeonBlue.copy(0.12f), Color.Transparent)
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Welcome back", color = Color.White.copy(0.6f), fontSize = 13.sp)
                    Text(
                        userName.ifEmpty { "Shield User" },
                        color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                        .border(1.dp, GlassBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.NotificationsNone, null, tint = NeonBlue, modifier = Modifier.size(22.dp))
                }
            }

            Spacer(Modifier.height(20.dp))

            // Trust score card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(NeonBlue.copy(0.15f), NeonPurple.copy(0.1f))
                        )
                    )
                    .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("AI Trust Score", color = Color.White.copy(0.7f), fontSize = 12.sp, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "$trustScore",
                                color = when {
                                    trustScore >= 80 -> RiskLow
                                    trustScore >= 50 -> RiskMedium
                                    else -> RiskHigh
                                },
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 48.sp
                            )
                            Text("/100", color = Color.White.copy(0.4f), fontSize = 18.sp, modifier = Modifier.padding(bottom = 6.dp, start = 4.dp))
                        }
                        Text(
                            when {
                                trustScore >= 80 -> "Excellent protection"
                                trustScore >= 50 -> "Moderate risk detected"
                                else -> "High threat exposure"
                            },
                            color = Color.White.copy(0.6f), fontSize = 12.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("$totalScans", color = NeonBlue, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text("total scans", color = Color.White.copy(0.5f), fontSize = 11.sp)
                        Spacer(Modifier.height(8.dp))
                        Icon(Icons.Default.Shield, null, tint = NeonBlue.copy(0.3f), modifier = Modifier.size(48.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun QuickStatsRow(fakeDetected: Int, suspicious: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatChip(label = "Fake Detected", value = "$fakeDetected", color = RiskHigh, modifier = Modifier.weight(1f))
        StatChip(label = "Suspicious", value = "$suspicious", color = RiskMedium, modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(0.1f))
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color.White.copy(0.6f), fontSize = 11.sp)
        }
    }
}

@Composable
fun ScanGrid(onScanClick: (ScanType) -> Unit) {
    val scanTypes = listOf(
        Triple(ScanType.IMAGE, "Scan Image", NeonBlue) to Icons.Default.Image,
        Triple(ScanType.VIDEO, "Scan Video", NeonPurple) to Icons.Default.VideoFile,
        Triple(ScanType.AUDIO, "Scan Audio", NeonCyan) to Icons.Default.Mic,
        Triple(ScanType.TEXT, "Scan Text", NeonPink) to Icons.Default.TextFields,
        Triple(ScanType.URL, "Scan URL", Color(0xFFFFB800)) to Icons.Default.Link,
        Triple(ScanType.PROFILE, "Scan Profile", Color(0xFF00E5FF)) to Icons.Default.Person
    )

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        scanTypes.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (triple, icon) ->
                    val (type, label, color) = triple
                    ScanTypeCard(
                        title = label,
                        subtitle = "Tap to scan",
                        icon = icon,
                        color = color,
                        onClick = { onScanClick(type) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(0.1f))
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
        Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RecentScanItem(
    mediaType: String,
    verdict: String,
    confidence: Float,
    timestamp: String,
    modifier: Modifier = Modifier
) {
    val icon = when (mediaType) {
        "IMAGE" -> Icons.Default.Image
        "VIDEO" -> Icons.Default.VideoFile
        "AUDIO" -> Icons.Default.Mic
        "TEXT" -> Icons.Default.TextFields
        "URL" -> Icons.Default.Link
        else -> Icons.Default.Shield
    }
    val verdictColor = when (verdict) {
        "FAKE" -> RiskHigh; "SUSPICIOUS" -> RiskMedium; else -> RiskLow
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(GlassWhite)
            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(verdictColor.copy(0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = verdictColor, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(mediaType, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(timestamp.take(16).replace("T", " "), color = Color.White.copy(0.5f), fontSize = 11.sp)
        }
        VerdictBadge(verdict)
    }
}

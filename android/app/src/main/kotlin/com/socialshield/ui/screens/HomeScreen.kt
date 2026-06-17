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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
    onScanDetailClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val safeCount = maxOf(0, uiState.totalScans - uiState.fakeDetected - uiState.suspiciousDetected)

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
                totalScans = uiState.totalScans,
                fakeDetected = uiState.fakeDetected,
                suspicious = uiState.suspiciousDetected,
                safe = safeCount,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(24.dp))
        }

        // Charts Section
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ScanTrendGraph(recentScans = uiState.recentScans)
                RiskDistributionCard(
                    fakeCount = uiState.fakeDetected,
                    suspiciousCount = uiState.suspiciousDetected,
                    totalCount = uiState.totalScans
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        // Scan type cards
        item {
            Text(
                "Scan & Detect",
                color = ContentColor,
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
                    Text("Live Activity Feed", color = ContentColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onHistoryClick) {
                        Text("View All", color = NeonBlue, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            items(uiState.recentScans.take(5)) { scan ->
                RecentScanItem(
                    mediaType = scan.mediaType,
                    verdict = scan.verdict,
                    timestamp = scan.timestamp,
                    onClick = { onScanDetailClick(scan.scanId) },
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
                    Text("Welcome back", color = ContentColor.copy(0.6f), fontSize = 13.sp)
                    Text(
                        userName.ifEmpty { "Shield User" },
                        color = ContentColor, fontSize = 22.sp, fontWeight = FontWeight.Bold
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AI Trust Score", color = ContentColor.copy(0.7f), fontSize = 12.sp, letterSpacing = 1.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            when {
                                trustScore >= 80 -> "Excellent protection"
                                trustScore >= 50 -> "Moderate risk detected"
                                else -> "High threat exposure"
                            },
                            color = ContentColor.copy(0.6f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Based on $totalScans scans analyzed by AI",
                            color = ContentColor.copy(0.4f), fontSize = 11.sp
                        )
                    }

                    CircularTrustScore(score = trustScore)
                }
            }
        }
    }
}

@Composable
fun CircularTrustScore(score: Int, modifier: Modifier = Modifier) {
    val animatedScore by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(durationMillis = 1200, easing = EaseOutCubic),
        label = "score"
    )
    val color = when {
        score >= 80 -> RiskLow
        score >= 50 -> RiskMedium
        else -> RiskHigh
    }
    
    Box(
        modifier = modifier.size(90.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { animatedScore },
            modifier = Modifier.fillMaxSize(),
            color = color,
            strokeWidth = 6.dp,
            trackColor = color.copy(alpha = 0.15f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = ContentColor
            )
            Text(
                text = "Score",
                fontSize = 9.sp,
                color = ContentColor.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ScanTrendGraph(recentScans: List<com.socialshield.domain.models.ScanHistoryItem>, modifier: Modifier = Modifier) {
    val scores = recentScans.map { 
        when (it.verdict) {
            "FAKE" -> 10f
            "SUSPICIOUS" -> 50f
            else -> 100f
        }
    }.reversed()
    
    val contentCol = ContentColor
    val primaryCol = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Security Trust Trend",
                color = ContentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
            
            if (scores.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No scans recorded yet", color = ContentColor.copy(alpha = 0.4f), fontSize = 12.sp)
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    
                    val points = mutableListOf<androidx.compose.ui.geometry.Offset>()
                    val stepX = if (scores.size > 1) width / (scores.size - 1) else width
                    
                    scores.forEachIndexed { index, score ->
                        val x = index * stepX
                        val y = height - (score / 100f * (height - 10f)) - 5f
                        points.add(androidx.compose.ui.geometry.Offset(x, y))
                    }
                    
                    // Draw grid lines
                    val gridCount = 2
                    for (i in 0..gridCount) {
                        val y = i * (height / gridCount)
                        drawLine(
                            color = contentCol.copy(alpha = 0.05f),
                            start = androidx.compose.ui.geometry.Offset(0f, y),
                            end = androidx.compose.ui.geometry.Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    
                    // Draw trend line
                    val path = Path()
                    if (points.isNotEmpty()) {
                        path.moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            path.lineTo(points[i].x, points[i].y)
                        }
                        drawPath(
                            path = path,
                            color = primaryCol,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        )
                        
                        // Draw point circles
                        points.forEach { pt ->
                            drawCircle(
                                color = primaryCol,
                                radius = 4.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = contentCol,
                                radius = 2.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RiskDistributionCard(fakeCount: Int, suspiciousCount: Int, totalCount: Int, modifier: Modifier = Modifier) {
    val safeCount = maxOf(0, totalCount - fakeCount - suspiciousCount)
    
    val fakePercent = if (totalCount > 0) (fakeCount.toFloat() / totalCount * 100).toInt() else 0
    val suspiciousPercent = if (totalCount > 0) (suspiciousCount.toFloat() / totalCount * 100).toInt() else 0
    val safePercent = if (totalCount > 0) (safeCount.toFloat() / totalCount * 100).toInt() else 0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Risk Exposure Breakdown",
                color = ContentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
            
            // Bar representation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                if (totalCount == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ContentColor.copy(alpha = 0.1f))
                    )
                } else {
                    if (safePercent > 0) {
                        Box(
                            modifier = Modifier
                                .weight(safePercent.toFloat())
                                .fillMaxHeight()
                                .background(RiskLow)
                        )
                    }
                    if (suspiciousPercent > 0) {
                        Box(
                            modifier = Modifier
                                .weight(suspiciousPercent.toFloat())
                                .fillMaxHeight()
                                .background(RiskMedium)
                        )
                    }
                    if (fakePercent > 0) {
                        Box(
                            modifier = Modifier
                                .weight(fakePercent.toFloat())
                                .fillMaxHeight()
                                .background(RiskHigh)
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Legends
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendItem(label = "Safe", value = "$safePercent%", color = RiskLow)
                LegendItem(label = "Suspicious", value = "$suspiciousPercent%", color = RiskMedium)
                LegendItem(label = "Fake", value = "$fakePercent%", color = RiskHigh)
            }
        }
    }
}

@Composable
fun LegendItem(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = "$label ($value)",
            fontSize = 12.sp,
            color = ContentColor.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun QuickStatsRow(totalScans: Int, fakeDetected: Int, suspicious: Int, safe: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatChip(label = "Total Scans", value = "$totalScans", color = NeonBlue, modifier = Modifier.weight(1f))
            StatChip(label = "Safe Profiles", value = "$safe", color = RiskLow, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatChip(label = "Suspicious", value = "$suspicious", color = RiskMedium, modifier = Modifier.weight(1f))
            StatChip(label = "Fake Detected", value = "$fakeDetected", color = RiskHigh, modifier = Modifier.weight(1f))
        }
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
            Text(label, color = ContentColor.copy(0.6f), fontSize = 11.sp)
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
        Text(label, color = ContentColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RecentScanItem(
    mediaType: String,
    verdict: String,
    timestamp: String,
    onClick: () -> Unit,
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
            .clickable { onClick() }
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
            Text(mediaType, color = ContentColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(timestamp.take(16).replace("T", " "), color = ContentColor.copy(0.5f), fontSize = 11.sp)
        }
        VerdictBadge(verdict)
    }
}

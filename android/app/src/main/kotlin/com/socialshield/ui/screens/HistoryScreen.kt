package com.socialshield.ui.screens

import androidx.compose.animation.*
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
import com.socialshield.ui.components.VerdictBadge
import com.socialshield.ui.theme.*
import com.socialshield.ui.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onScanDetail: (String) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    val filters = listOf("ALL", "IMAGE", "VIDEO", "AUDIO", "TEXT", "URL")

    LaunchedEffect(selectedFilter) { viewModel.loadHistory(if (selectedFilter == "ALL") null else selectedFilter) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepBlack, DarkSurface)))
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp).clip(CircleShape).background(GlassWhite)
            ) {
                Icon(Icons.Default.ArrowBack, null, tint = Color(0xFF0F172A), modifier = Modifier.size(20.dp))
            }
            Text("Scan History", color = Color(0xFF0F172A), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // Filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            items(filters) { filter ->
                val selected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) NeonBlue.copy(0.25f) else GlassWhite)
                        .border(1.dp, if (selected) NeonBlue.copy(0.6f) else GlassBorder, RoundedCornerShape(20.dp))
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(filter, color = if (selected) NeonBlue else Color(0xFF0F172A).copy(0.7f), fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonBlue)
            }
        } else if (uiState.scans.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.SearchOff, null, tint = Color(0xFF0F172A).copy(0.3f), modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("No scans found", color = Color(0xFF0F172A).copy(0.4f), fontSize = 16.sp)
                    Text("Start scanning to see results here", color = Color(0xFF0F172A).copy(0.3f), fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.scans, key = { it.scanId }) { scan ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically { 20 } + fadeIn()
                    ) {
                        HistoryItem(
                            scan = scan,
                            onClick = { onScanDetail(scan.scanId) },
                            onDelete = { viewModel.deleteScan(scan.scanId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(
    scan: com.socialshield.domain.models.ScanHistoryItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val verdictColor = when (scan.verdict) {
        "FAKE" -> RiskHigh; "SUSPICIOUS" -> RiskMedium; else -> RiskLow
    }
    val icon = when (scan.mediaType) {
        "IMAGE" -> Icons.Default.Image; "VIDEO" -> Icons.Default.VideoFile; "AUDIO" -> Icons.Default.Mic
        "TEXT" -> Icons.Default.TextFields; "URL" -> Icons.Default.Link; else -> Icons.Default.Shield
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(verdictColor.copy(0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = verdictColor, modifier = Modifier.size(22.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(scan.mediaType, color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(
                scan.timestamp.take(16).replace("T", " "),
                color = Color(0xFF0F172A).copy(0.45f), fontSize = 11.sp
            )
        }

        VerdictBadge(scan.verdict)

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.DeleteOutline, null, tint = Color(0xFF0F172A).copy(0.3f), modifier = Modifier.size(18.dp))
        }
    }
}

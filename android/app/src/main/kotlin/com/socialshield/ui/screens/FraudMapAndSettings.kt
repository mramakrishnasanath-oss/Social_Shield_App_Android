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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.socialshield.ui.components.GlassCard
import com.socialshield.ui.components.NeonButton
import com.socialshield.ui.theme.*

// ─── Fraud Map Screen ─────────────────────────────────────────────────────────

data class FraudReport(val city: String, val country: String, val count: Int, val type: String, val severity: String)

private val SAMPLE_REPORTS = listOf(
    FraudReport("Mumbai", "India", 342, "Phishing SMS", "HIGH"),
    FraudReport("Lagos", "Nigeria", 218, "Romance Scam", "HIGH"),
    FraudReport("London", "UK", 156, "Deepfake Video", "MEDIUM"),
    FraudReport("New York", "USA", 189, "Voice Cloning", "HIGH"),
    FraudReport("Beijing", "China", 134, "Fake Profile", "MEDIUM"),
    FraudReport("São Paulo", "Brazil", 97, "Crypto Scam", "HIGH"),
    FraudReport("Jakarta", "Indonesia", 88, "Phishing URL", "MEDIUM"),
    FraudReport("Nairobi", "Kenya", 75, "OTP Fraud", "HIGH"),
    FraudReport("Dhaka", "Bangladesh", 67, "Fake Lottery", "LOW"),
    FraudReport("Mexico City", "Mexico", 61, "Identity Theft", "MEDIUM")
)

@Composable
fun FraudMapScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepBlack, DarkSurface)))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).clip(CircleShape).background(GlassWhite)) {
                Icon(Icons.Default.ArrowBack, null, tint = Color(0xFF0F172A), modifier = Modifier.size(20.dp))
            }
            Column {
                Text("Global Fraud Map", color = Color(0xFF0F172A), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Live threat intelligence", color = Color(0xFF0F172A).copy(0.5f), fontSize = 12.sp)
            }
        }

        // Heat indicator legend
        GlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("LOW" to RiskLow, "MEDIUM" to RiskMedium, "HIGH" to RiskHigh).forEach { (label, color) ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
                        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("10,247", color = NeonBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("reports today", color = Color(0xFF0F172A).copy(0.5f), fontSize = 10.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Simulated world map (heatmap cells)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0A0A2E))
                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                // Draw simplified world map dots
                val hotspots = listOf(
                    Offset(0.72f, 0.45f) to RiskHigh,    // India
                    Offset(0.52f, 0.6f) to RiskHigh,     // Nigeria
                    Offset(0.48f, 0.32f) to RiskMedium,  // UK
                    Offset(0.2f, 0.38f) to RiskHigh,     // New York
                    Offset(0.82f, 0.42f) to RiskMedium,  // Beijing
                    Offset(0.3f, 0.65f) to RiskHigh,     // Brazil
                    Offset(0.78f, 0.56f) to RiskMedium,  // Jakarta
                    Offset(0.56f, 0.56f) to RiskHigh,    // Kenya
                )
                hotspots.forEach { (pos, color) ->
                    repeat(3) { ring ->
                        drawCircle(
                            color.copy(alpha = 0.15f / (ring + 1)),
                            radius = (20f + ring * 15f),
                            center = Offset(size.width * pos.x, size.height * pos.y)
                        )
                    }
                    drawCircle(color, radius = 6f, center = Offset(size.width * pos.x, size.height * pos.y))
                }
            }
            Text("Interactive Map · Tap reports for details", color = Color(0xFF0F172A).copy(0.3f), fontSize = 11.sp)
        }

        Spacer(Modifier.height(16.dp))

        Text("Top Fraud Hotspots", color = Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SAMPLE_REPORTS) { report ->
                val color = when (report.severity) { "HIGH" -> RiskHigh; "MEDIUM" -> RiskMedium; else -> RiskLow }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(GlassWhite)
                        .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(Modifier.size(40.dp).clip(CircleShape).background(color.copy(0.15f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Warning, null, tint = color, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${report.city}, ${report.country}", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(report.type, color = Color(0xFF0F172A).copy(0.5f), fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${report.count}", color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("reports", color = Color(0xFF0F172A).copy(0.4f), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

// ─── Settings Screen ──────────────────────────────────────────────────────────

@Composable
fun SettingsScreen(onBack: () -> Unit, onSignOut: () -> Unit) {
    val darkMode by ThemeState.isDarkMode.collectAsState()
    var notifications by remember { mutableStateOf(true) }
    var localProcessing by remember { mutableStateOf(false) }
    var autoSave by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepBlack, DarkSurface)))
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).clip(CircleShape).background(GlassWhite)) {
                Icon(Icons.Default.ArrowBack, null, tint = Color(0xFF0F172A), modifier = Modifier.size(20.dp))
            }
            Text("Settings", color = Color(0xFF0F172A), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SettingsSection("Appearance") {
                SettingsToggle("Dark Mode", "Use dark cybersecurity theme", Icons.Default.DarkMode, NeonBlue, darkMode) { ThemeState.setDarkMode(it) }
            }

            SettingsSection("Privacy & Security") {
                SettingsToggle("Local Processing", "Process media on-device when possible", Icons.Default.PhonelinkLock, NeonPurple, localProcessing) { localProcessing = it }
                SettingsToggle("Auto-Save Scans", "Save scan results to history", Icons.Default.Save, NeonCyan, autoSave) { autoSave = it }
            }

            SettingsSection("Notifications") {
                SettingsToggle("Threat Alerts", "Notify on new fraud threats in your area", Icons.Default.NotificationsActive, NeonPink, notifications) { notifications = it }
            }

            SettingsSection("Account") {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val user = FirebaseAuth.getInstance().currentUser
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.size(44.dp).clip(CircleShape).background(NeonBlue.copy(0.2f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, tint = NeonBlue, modifier = Modifier.size(24.dp))
                            }
                            Column {
                                Text(user?.displayName ?: "Shield User", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                Text(user?.email ?: "", color = Color(0xFF0F172A).copy(0.5f), fontSize = 12.sp)
                            }
                        }
                        HorizontalDivider(color = GlassBorder)
                        TextButton(
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                onSignOut()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Logout, null, tint = RiskHigh, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Sign Out", color = RiskHigh, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // App info
            GlassCard {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("SocialShield", color = Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Version 1.0.0", color = Color(0xFF0F172A).copy(0.5f), fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("AI-Powered Digital Fraud Protection", color = NeonBlue.copy(0.7f), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = NeonBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        content()
    }
}

@Composable
fun SettingsToggle(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(Modifier.size(40.dp).clip(CircleShape).background(color.copy(0.15f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, color = Color(0xFF0F172A).copy(0.5f), fontSize = 11.sp, lineHeight = 14.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF0F172A),
                    checkedTrackColor = color,
                    uncheckedThumbColor = Color(0xFF0F172A).copy(0.5f),
                    uncheckedTrackColor = GlassWhite
                )
            )
        }
    }
}

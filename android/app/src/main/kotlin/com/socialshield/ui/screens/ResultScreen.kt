package com.socialshield.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.socialshield.ui.components.*
import com.socialshield.ui.theme.*
import com.socialshield.ui.viewmodel.ResultViewModel

@Composable
fun ResultScreen(
    scanId: String,
    onBack: () -> Unit,
    onScanAgain: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val result by viewModel.result.collectAsState()
    val error by viewModel.error.collectAsState()
    var showHeatmap by remember { mutableStateOf(false) }

    LaunchedEffect(scanId) { viewModel.loadResult(scanId) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepBlack, DarkSurface)))
    ) {
        if (error != null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.ErrorOutline, null, tint = RiskHigh, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text(error!!, color = Color(0xFF0F172A), fontSize = 16.sp)
                Spacer(Modifier.height(24.dp))
                NeonButton(text = "Go Back", onClick = onBack)
            }
        } else if (result == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonBlue)
            }
        } else {
            val r = result!!
            val verdictColor = when (r.verdict) {
                "FAKE" -> RiskHigh; "SUSPICIOUS" -> RiskMedium; else -> RiskLow
            }
            val verdictBg = when (r.verdict) {
                "FAKE" -> RiskHigh.copy(0.08f); "SUSPICIOUS" -> RiskMedium.copy(0.08f); else -> RiskLow.copy(0.08f)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top bar
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
                    Text("Scan Result", color = Color(0xFF0F172A), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                // Big verdict section
                AnimatedVisibility(visible = true, enter = scaleIn(spring(dampingRatio = 0.6f)) + fadeIn()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(verdictBg)
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Verdict icon
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(CircleShape)
                                    .background(verdictColor.copy(0.15f))
                                    .border(2.dp, verdictColor.copy(0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    when (r.verdict) {
                                        "FAKE" -> Icons.Default.GppBad
                                        "SUSPICIOUS" -> Icons.Default.GppMaybe
                                        else -> Icons.Default.GppGood
                                    },
                                    null,
                                    tint = verdictColor,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                            Spacer(Modifier.height(20.dp))
                            Text(
                                r.verdict,
                                color = verdictColor,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 3.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            ConfidenceArc(percentage = r.confidence, verdict = r.verdict)
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Risk level
                    GlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Risk Level", color = Color(0xFF0F172A).copy(0.6f), fontSize = 12.sp)
                                Spacer(Modifier.height(8.dp))
                                RiskIndicator(r.riskLevel)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Fake Probability", color = Color(0xFF0F172A).copy(0.6f), fontSize = 12.sp)
                                Text(
                                    "${r.fakeProbability.toInt()}%",
                                    color = verdictColor,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Probability bars
                    GlassCard {
                        Text("Probability Analysis", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(16.dp))
                        ProbabilityBar(label = "Fake", value = r.fakeProbability, color = RiskHigh)
                        Spacer(Modifier.height(8.dp))
                        ProbabilityBar(label = "Real", value = r.realProbability, color = RiskLow)
                    }

                    // Heatmap (if available)
                    r.heatmapBase64?.let { heatmapB64 ->
                        GlassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Manipulation Heatmap", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Red areas indicate manipulation", color = Color(0xFF0F172A).copy(0.5f), fontSize = 12.sp)
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
                            Text("AI Explanation", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(14.dp))
                        r.explanations.forEach { explanation ->
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
                                    color = Color(0xFF0F172A).copy(0.85f),
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    // Metadata
                    r.metadata?.let { meta ->
                        GlassCard {
                            Text("Technical Details", color = Color(0xFF0F172A).copy(0.7f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(10.dp))
                            meta.entries.take(5).forEach { (key, value) ->
                                if (value !is Map<*, *> && value !is List<*>) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(key.replace("_", " ").replaceFirstChar { it.uppercase() }, color = Color(0xFF0F172A).copy(0.5f), fontSize = 12.sp)
                                        Text("$value", color = Color(0xFF0F172A).copy(0.8f), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Scan ID
                    Text(
                        "Scan ID: ${scanId.take(8)}…",
                        color = Color(0xFF0F172A).copy(0.3f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Actions
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, GlassBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F172A))
                        ) {
                            Text("← Back")
                        }
                        NeonButton(
                            text = "Scan Again",
                            onClick = onScanAgain,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProbabilityBar(label: String, value: Float, color: Color) {
    val animatedWidth by animateFloatAsState(value / 100f, tween(1000, easing = EaseOutCubic), label = "bar")
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color(0xFF0F172A).copy(0.7f), fontSize = 13.sp)
            Text("${value.toInt()}%", color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color.copy(0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Brush.horizontalGradient(listOf(color.copy(0.7f), color)))
            )
        }
    }
}

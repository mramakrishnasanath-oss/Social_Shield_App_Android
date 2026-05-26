package com.socialshield.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.socialshield.ui.theme.*

private val ANALYSIS_STEPS = listOf(
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

@Composable
fun ProcessingScreen() {
    var currentStep by remember { mutableStateOf(0) }
    var completedSteps by remember { mutableStateOf(setOf<Int>()) }

    val infiniteTransition = rememberInfiniteTransition(label = "proc")
    val rotationAngle by infiniteTransition.animateFloat(
        0f, 360f, infiniteRepeatable(tween(2000, easing = LinearEasing)), "rot"
    )
    val outerRotation by infiniteTransition.animateFloat(
        360f, 0f, infiniteRepeatable(tween(3000, easing = LinearEasing)), "outerRot"
    )
    val pulseScale by infiniteTransition.animateFloat(
        0.92f, 1.08f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), "pulse"
    )

    LaunchedEffect(Unit) {
        ANALYSIS_STEPS.indices.forEach { i ->
            delay(600L)
            completedSteps = completedSteps + (i - 1)
            currentStep = i
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(
                listOf(Color(0xFF080820), DeepBlack, Color(0xFF050515)),
                radius = 1500f
            )),
        contentAlignment = Alignment.Center
    ) {
        // Background particles
        Canvas(Modifier.fillMaxSize()) {
            repeat(20) { i ->
                val x = (i * 137.5f) % size.width
                val y = (i * 73.1f) % size.height
                drawCircle(NeonBlue.copy(0.06f + (i % 3) * 0.02f), radius = 2f + (i % 4), center = Offset(x, y))
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Central animation
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val r = size.minDimension / 2

                    // Outer ring
                    drawArc(
                        brush = Brush.sweepGradient(listOf(Color.Transparent, NeonBlue.copy(0.4f), NeonBlue)),
                        startAngle = rotationAngle,
                        sweepAngle = 240f,
                        useCenter = false,
                        topLeft = Offset(cx - r * 0.9f, cy - r * 0.9f),
                        size = androidx.compose.ui.geometry.Size(r * 1.8f, r * 1.8f),
                        style = Stroke(3f, cap = StrokeCap.Round)
                    )

                    // Inner ring
                    drawArc(
                        color = NeonPurple.copy(0.6f),
                        startAngle = outerRotation,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(cx - r * 0.65f, cy - r * 0.65f),
                        size = androidx.compose.ui.geometry.Size(r * 1.3f, r * 1.3f),
                        style = Stroke(2f, cap = StrokeCap.Round)
                    )

                    // Center glow
                    drawCircle(
                        Brush.radialGradient(
                            listOf(NeonBlue.copy(0.3f), NeonPurple.copy(0.1f), Color.Transparent),
                            center = Offset(cx, cy), radius = r * 0.4f
                        ),
                        radius = r * 0.4f, center = Offset(cx, cy)
                    )

                    // Scanning crosshair lines
                    val lineLen = r * 0.25f
                    val crossColor = NeonBlue.copy(0.5f)
                    drawLine(crossColor, Offset(cx - lineLen, cy), Offset(cx + lineLen, cy), 1.5f)
                    drawLine(crossColor, Offset(cx, cy - lineLen), Offset(cx, cy + lineLen), 1.5f)
                    drawCircle(NeonBlue.copy(0.9f), radius = 6f, center = Offset(cx, cy))
                }

                Text(
                    "AI",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Analyzing…",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Our AI is examining your content",
                color = Color.White.copy(0.5f),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(40.dp))

            // Steps list
            Column(
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassWhite)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ANALYSIS_STEPS.take(currentStep + 1).forEachIndexed { i, step ->
                    val isDone = i in completedSteps
                    val isCurrent = i == currentStep

                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically { 20 } + fadeIn()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (isDone) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    null,
                                    tint = RiskLow,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else if (isCurrent) {
                                val dotAnim by rememberInfiniteTransition(label = "dot").animateFloat(
                                    0.3f, 1f, infiniteRepeatable(tween(500), RepeatMode.Reverse), "da"
                                )
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(NeonBlue.copy(dotAnim))
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(GlassWhite)
                                )
                            }

                            Text(
                                step,
                                color = when {
                                    isDone -> Color.White.copy(0.5f)
                                    isCurrent -> Color.White
                                    else -> Color.White.copy(0.3f)
                                },
                                fontSize = 13.sp,
                                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

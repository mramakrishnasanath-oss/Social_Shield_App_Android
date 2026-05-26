package com.socialshield.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.socialshield.ui.theme.*

@Composable
fun SplashScreen(onNavigateNext: () -> Unit) {
    var logoVisible by remember { mutableStateOf(false) }
    var taglineVisible by remember { mutableStateOf(false) }
    var scanLinePos by remember { mutableStateOf(0f) }

    val logoScale by animateFloatAsState(
        if (logoVisible) 1f else 0.3f,
        spring(dampingRatio = 0.6f, stiffness = 200f), label = "logo"
    )
    val logoAlpha by animateFloatAsState(if (logoVisible) 1f else 0f, tween(600), label = "logoAlpha")
    val taglineAlpha by animateFloatAsState(if (taglineVisible) 1f else 0f, tween(800), label = "tag")

    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLine by infiniteTransition.animateFloat(
        -0.1f, 1.1f,
        infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart), "scanLine"
    )
    val glowPulse by infiniteTransition.animateFloat(
        0.4f, 1f,
        infiniteRepeatable(tween(1500), RepeatMode.Reverse), "glow"
    )
    val rotationAnim by infiniteTransition.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(8000, easing = LinearEasing)), "rot"
    )

    LaunchedEffect(Unit) {
        delay(300)
        logoVisible = true
        delay(800)
        taglineVisible = true
        delay(1800)
        onNavigateNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(
                colors = listOf(Color(0xFF0A0A2E), DeepBlack, Color(0xFF05050F)),
                center = Offset(0.5f, 0.4f),
                radius = 1200f
            )),
        contentAlignment = Alignment.Center
    ) {
        // Background grid lines
        Canvas(Modifier.fillMaxSize()) {
            val gridColor = NeonBlue.copy(alpha = 0.04f)
            val spacing = 40.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), 1f)
                x += spacing
            }
            var y = 0f
            while (y < size.height) {
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), 1f)
                y += spacing
            }
            // Scanning line
            val scanY = scanLine * size.height
            drawLine(
                Brush.horizontalGradient(listOf(Color.Transparent, NeonBlue.copy(0.6f), Color.Transparent)),
                Offset(0f, scanY), Offset(size.width, scanY), 2f
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Animated shield logo
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val r = size.minDimension / 2

                    // Outer glow rings
                    repeat(3) { i ->
                        drawCircle(
                            NeonBlue.copy(alpha = glowPulse * 0.15f / (i + 1)),
                            radius = r * (1f + i * 0.25f),
                            center = Offset(cx, cy)
                        )
                    }

                    // Rotating dashes ring
                    drawArc(
                        color = NeonBlue.copy(0.5f),
                        startAngle = rotationAnim,
                        sweepAngle = 120f,
                        useCenter = false,
                        topLeft = Offset(cx - r * 0.85f, cy - r * 0.85f),
                        size = androidx.compose.ui.geometry.Size(r * 1.7f, r * 1.7f),
                        style = Stroke(3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f)))
                    )
                    drawArc(
                        color = NeonPurple.copy(0.5f),
                        startAngle = rotationAnim + 180f,
                        sweepAngle = 120f,
                        useCenter = false,
                        topLeft = Offset(cx - r * 0.85f, cy - r * 0.85f),
                        size = androidx.compose.ui.geometry.Size(r * 1.7f, r * 1.7f),
                        style = Stroke(3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f)))
                    )

                    // Shield fill
                    drawCircle(
                        Brush.radialGradient(
                            listOf(NeonPurple.copy(0.4f), NeonBlue.copy(0.2f), Color.Transparent),
                            center = Offset(cx, cy), radius = r * 0.7f
                        ),
                        radius = r * 0.7f, center = Offset(cx, cy)
                    )

                    // Shield border
                    drawCircle(
                        Brush.sweepGradient(listOf(NeonBlue, NeonPurple, NeonCyan, NeonBlue)),
                        radius = r * 0.7f, center = Offset(cx, cy),
                        style = Stroke(2.5f)
                    )
                }

                // SS text in center
                Text(
                    "SS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                "SocialShield",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp,
                modifier = Modifier.alpha(logoAlpha)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Verify Reality with AI",
                color = NeonBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 3.sp,
                modifier = Modifier.alpha(taglineAlpha)
            )

            Spacer(Modifier.height(60.dp))

            // Loading dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.alpha(taglineAlpha)
            ) {
                repeat(3) { i ->
                    val dotAnim by rememberInfiniteTransition(label = "dot$i").animateFloat(
                        0.3f, 1f,
                        infiniteRepeatable(tween(600, delayMillis = i * 200), RepeatMode.Reverse),
                        "dotA$i"
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(NeonBlue.copy(dotAnim), androidx.compose.foundation.shape.CircleShape)
                    )
                }
            }
        }
    }
}

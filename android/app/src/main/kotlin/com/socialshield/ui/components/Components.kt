package com.socialshield.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.socialshield.ui.theme.*

// ─── Glass Card ───────────────────────────────────────────────────────────────

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val baseModifier = modifier
        .clip(RoundedCornerShape(20.dp))
        .background(GlassWhite)
        .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))

    val finalModifier = if (onClick != null) {
        baseModifier.clickable { onClick() }
    } else baseModifier

    Column(modifier = finalModifier.padding(16.dp), content = content)
}

// ─── Neon Button ─────────────────────────────────────────────────────────────

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = NeonBlue,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.horizontalGradient(listOf(color, NeonPurple))
            )
            .then(
                if (enabled && !isLoading) Modifier.clickable { onClick() }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

// ─── Scan Type Card ───────────────────────────────────────────────────────────

@Composable
fun ScanTypeCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "scale")

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(color.copy(alpha = 0.2f), color.copy(alpha = 0.05f))
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(listOf(color.copy(0.6f), color.copy(0.1f))),
                RoundedCornerShape(20.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(20.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(subtitle, color = Color.White.copy(0.6f), fontSize = 12.sp)
        }
    }
}

// ─── Verdict Badge ────────────────────────────────────────────────────────────

@Composable
fun VerdictBadge(verdict: String, modifier: Modifier = Modifier) {
    val (color, text) = when (verdict.uppercase()) {
        "FAKE" -> RiskHigh to "● FAKE"
        "SUSPICIOUS" -> RiskMedium to "◆ SUSPICIOUS"
        else -> RiskLow to "✓ REAL"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color.copy(0.15f))
            .border(1.dp, color.copy(0.5f), RoundedCornerShape(50.dp))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(text, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

// ─── Confidence Arc ───────────────────────────────────────────────────────────

@Composable
fun ConfidenceArc(
    percentage: Float,
    verdict: String,
    modifier: Modifier = Modifier
) {
    val color = when (verdict.uppercase()) {
        "FAKE" -> RiskHigh
        "SUSPICIOUS" -> RiskMedium
        else -> RiskLow
    }
    val animatedPct by animateFloatAsState(percentage / 100f, tween(1200), label = "arc")

    Box(modifier = modifier.size(180.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 14.dp.toPx()
            val padding = stroke / 2
            val arcRect = androidx.compose.ui.geometry.Rect(
                Offset(padding, padding),
                androidx.compose.ui.geometry.Size(size.width - stroke, size.height - stroke)
            )
            // Background track
            drawArc(
                color = Color.White.copy(0.08f),
                startAngle = 135f, sweepAngle = 270f,
                useCenter = false, topLeft = arcRect.topLeft,
                size = arcRect.size,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
            // Progress arc
            drawArc(
                brush = Brush.sweepGradient(listOf(color.copy(0.5f), color)),
                startAngle = 135f, sweepAngle = 270f * animatedPct,
                useCenter = false, topLeft = arcRect.topLeft,
                size = arcRect.size,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${percentage.toInt()}%",
                color = color,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp
            )
            Text("confidence", color = Color.White.copy(0.5f), fontSize = 12.sp)
        }
    }
}

// ─── Risk Level Indicator ─────────────────────────────────────────────────────

@Composable
fun RiskIndicator(risk: String, modifier: Modifier = Modifier) {
    val (color, label) = when (risk.uppercase()) {
        "HIGH" -> RiskHigh to "HIGH RISK"
        "MEDIUM" -> RiskMedium to "MEDIUM RISK"
        else -> RiskLow to "LOW RISK"
    }
    val levels = 3
    val filled = when (risk.uppercase()) { "HIGH" -> 3; "MEDIUM" -> 2; else -> 1 }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(levels) { i ->
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (i < filled) color else color.copy(0.2f))
                )
            }
        }
        Text(label, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

// ─── Scanning Animation ───────────────────────────────────────────────────────

@Composable
fun ScanningPulse(color: Color = NeonBlue, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale1 by infiniteTransition.animateFloat(
        1f, 2.5f, infiniteRepeatable(tween(1500, easing = EaseOutCubic), RepeatMode.Restart), "s1"
    )
    val scale2 by infiniteTransition.animateFloat(
        1f, 2.5f, infiniteRepeatable(tween(1500, 500, easing = EaseOutCubic), RepeatMode.Restart), "s2"
    )
    val alpha1 by infiniteTransition.animateFloat(0.6f, 0f, infiniteRepeatable(tween(1500)), "a1")
    val alpha2 by infiniteTransition.animateFloat(0.6f, 0f, infiniteRepeatable(tween(1500, 500)), "a2")

    Box(modifier = modifier.size(80.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(color.copy(alpha1), radius = size.minDimension / 2 * scale1)
            drawCircle(color.copy(alpha2), radius = size.minDimension / 2 * scale2)
        }
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape)
                .background(Brush.radialGradient(listOf(color, NeonPurple)))
        )
    }
}

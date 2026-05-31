package com.socialshield.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.socialshield.ui.components.NeonButton
import com.socialshield.ui.theme.*

data class OnboardingPage(
    val icon: ImageVector,
    val iconColor: Color,
    val title: String,
    val subtitle: String,
    val features: List<String>
)

private val pages = listOf(
    OnboardingPage(
        Icons.Default.Shield,
        NeonBlue,
        "AI-Powered Protection",
        "Real-time detection of deepfakes, scams, and digital manipulation using advanced neural networks.",
        listOf("Deepfake image & video detection", "AI voice clone identification", "Phishing URL scanning")
    ),
    OnboardingPage(
        Icons.Default.Visibility,
        NeonPurple,
        "See Through Deception",
        "Explainable AI highlights exactly what was manipulated and why it was flagged as fake.",
        listOf("Grad-CAM heatmap overlays", "Detailed manipulation reports", "Confidence scoring")
    ),
    OnboardingPage(
        Icons.Default.Public,
        NeonCyan,
        "Global Fraud Intelligence",
        "Crowd-sourced threat database and live fraud heatmap keep you ahead of emerging threats.",
        listOf("Global fraud heatmap", "Community threat reporting", "Live scam alerts")
    ),
    OnboardingPage(
        Icons.Default.VerifiedUser,
        NeonPink,
        "Your Digital Truth Score",
        "Every scan contributes to your personal AI Trust Score — know your digital safety at a glance.",
        listOf("Personal trust score", "Scan history & analytics", "Privacy-first processing")
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepBlack, DarkSurface, DeepBlack)))
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            OnboardingPageContent(pages[page])
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(pages.size) { i ->
                    val selected = pagerState.currentPage == i
                    val width by animateDpAsState(if (selected) 28.dp else 8.dp, label = "dot")
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (selected) NeonBlue else NeonBlue.copy(0.3f))
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            if (pagerState.currentPage < pages.size - 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onGetStarted) {
                        Text("Skip", color = Color(0xFF0F172A).copy(0.5f), fontSize = 14.sp)
                    }
                    NeonButton(
                        text = "Next →",
                        onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                        modifier = Modifier.width(140.dp)
                    )
                }
            } else {
                NeonButton(
                    text = "Get Started",
                    onClick = onGetStarted,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(page) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 100.dp, bottom = 160.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon
        AnimatedVisibility(visible, enter = scaleIn(spring()) + fadeIn()) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(
                        listOf(page.iconColor.copy(0.3f), page.iconColor.copy(0.05f))
                    ))
                    .border(1.dp, page.iconColor.copy(0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(page.icon, null, tint = page.iconColor, modifier = Modifier.size(52.dp))
            }
        }

        Spacer(Modifier.height(40.dp))

        AnimatedVisibility(visible, enter = slideInVertically { 40 } + fadeIn(tween(600, 200))) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    page.title,
                    color = Color(0xFF0F172A),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    page.subtitle,
                    color = Color(0xFF0F172A).copy(0.65f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        AnimatedVisibility(visible, enter = slideInVertically { 60 } + fadeIn(tween(600, 400))) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                page.features.forEach { feature ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier.size(8.dp).clip(CircleShape)
                                .background(page.iconColor)
                        )
                        Text(feature, color = Color(0xFF0F172A).copy(0.75f), fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

package com.socialshield.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.socialshield.ui.components.*
import com.socialshield.ui.theme.*
import com.socialshield.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isSignUp by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onAuthSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepBlack, DarkSurface, DeepBlack)))
    ) {
        // Background decorations
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset((-80).dp, (-80).dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(NeonBlue.copy(0.05f))
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(80.dp, 80.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(NeonPurple.copy(0.06f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // Logo
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Brush.radialGradient(listOf(NeonBlue.copy(0.4f), NeonPurple.copy(0.2f)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Shield, null, tint = NeonBlue, modifier = Modifier.size(24.dp))
                }
                Text("SocialShield", color = Color(0xFF0F172A), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(48.dp))

            Text(
                if (isSignUp) "Create Account" else "Welcome Back",
                color = Color(0xFF0F172A), fontSize = 28.sp, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (isSignUp) "Join the fight against digital fraud" else "Sign in to your secure dashboard",
                color = Color(0xFF0F172A).copy(0.55f), fontSize = 14.sp, textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // Email field
            ShieldTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = { Icon(Icons.Default.Email, null, tint = NeonBlue.copy(0.7f), modifier = Modifier.size(20.dp)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(Modifier.height(16.dp))

            // Password field
            ShieldTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = NeonBlue.copy(0.7f), modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            null, tint = Color(0xFF0F172A).copy(0.5f), modifier = Modifier.size(20.dp)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (isSignUp) viewModel.signUp(email, password)
                    else viewModel.signIn(email, password)
                })
            )

            // Error message
            uiState.error?.let { error ->
                Spacer(Modifier.height(12.dp))
                Text(error, color = RiskHigh, fontSize = 13.sp, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(28.dp))

            // Submit button
            NeonButton(
                text = if (isSignUp) "Create Account" else "Sign In",
                onClick = {
                    if (isSignUp) viewModel.signUp(email, password)
                    else viewModel.signIn(email, password)
                },
                modifier = Modifier.fillMaxWidth(),
                isLoading = uiState.isLoading
            )

            Spacer(Modifier.height(20.dp))

            // Divider
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = GlassBorder)
                Text("or", color = Color(0xFF0F172A).copy(0.4f), fontSize = 13.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = GlassBorder)
            }

            Spacer(Modifier.height(20.dp))

            // Google sign in
            OutlinedButton(
                onClick = { viewModel.signInWithGoogle() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, GlassBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F172A))
            ) {
                Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("Continue with Google", fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(24.dp))

            TextButton(onClick = { isSignUp = !isSignUp }) {
                Text(
                    if (isSignUp) "Already have an account? Sign In" else "Don't have an account? Sign Up",
                    color = NeonBlue, fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ShieldTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonBlue,
            unfocusedBorderColor = GlassBorder,
            focusedLabelColor = NeonBlue,
            unfocusedLabelColor = Color(0xFF0F172A).copy(0.5f),
            cursorColor = NeonBlue,
            focusedTextColor = Color(0xFF0F172A),
            unfocusedTextColor = Color(0xFF0F172A),
            focusedContainerColor = GlassWhite,
            unfocusedContainerColor = GlassWhite
        ),
        singleLine = true
    )
}

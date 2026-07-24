package com.example.auraai.presentation.onboarding.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.auraai.presentation.onboarding.OnboardingUiState
import com.example.auraai.theme.*
import kotlinx.coroutines.delay

@Composable
fun ProfileFormStep(
    uiState: OnboardingUiState,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Tell us about yourself",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        GlassTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = "Name",
            placeholder = "Enter your name"
        )

        GlassTextField(
            value = uiState.age,
            onValueChange = onAgeChange,
            label = "Age",
            placeholder = "How old are you?",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        GlassTextField(
            value = uiState.phone,
            onValueChange = onPhoneChange,
            label = "Phone Number",
            placeholder = "10 digit number",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        AnimatedVisibility(
            visible = uiState.phone.length == 10,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(16.dp))
                OtpInput(
                    otp = uiState.otp,
                    onOtpChange = onOtpChange,
                    isError = uiState.otpError != null
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            enabled = uiState.isProfileValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = if (uiState.isProfileValid) Brush.horizontalGradient(GradientPrimary) 
                            else Brush.horizontalGradient(listOf(GlassWhite, GlassWhite)),
                    shape = RoundedCornerShape(24.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                "Continue",
                style = MaterialTheme.typography.titleMedium,
                color = if (uiState.isProfileValid) Color.White else TextSecondary
            )
        }
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = TextSecondary,
            modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.5f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GlassWhite.copy(alpha = 0.05f),
                unfocusedContainerColor = GlassWhite.copy(alpha = 0.05f),
                disabledContainerColor = GlassWhite.copy(alpha = 0.05f),
                focusedBorderColor = AuraBlue,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = ErrorRed,
                cursorColor = AuraBlue,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(24.dp),
            isError = error != null,
            singleLine = true
        )
        AnimatedVisibility(visible = error != null) {
            Text(
                text = error ?: "",
                color = ErrorRed,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun OtpInput(
    otp: String,
    onOtpChange: (String) -> Unit,
    isError: Boolean
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var isShaking by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(600) // Wait for pager animations to complete before grabbing focus
        try {
            focusRequester.requestFocus()
            keyboardController?.show()
        } catch (e: Exception) {
            // Ignore focus errors if not visible
        }
    }
    
    LaunchedEffect(isError) {
        if (isError) {
            isShaking = true
            delay(500)
            isShaking = false
        }
    }

    val shakeOffset by animateFloatAsState(
        targetValue = if (isShaking) 10f else 0f,
        animationSpec = if (isShaking) repeatable(
            iterations = 5,
            animation = tween(durationMillis = 50, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ) else tween(0),
        label = "shake"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("Verification Code", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Enter the 4-digit verification code", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            color = GlassWhite.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Demo OTP: 1234",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                color = AuraBlue
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.offset(x = shakeOffset.dp)
        ) {
            BasicTextField(
                value = otp,
                onValueChange = { 
                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                        onOtpChange(it)
                    }
                },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .alpha(0.01f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val char = otp.getOrNull(i)?.toString() ?: ""
                    val isFocused = otp.length == i || (otp.length == 4 && i == 3)
                    val isFilled = char.isNotEmpty()
                    
                    val scale by animateFloatAsState(
                        targetValue = if (isFocused && otp.length < 4) 1.05f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "scale"
                    )
                    
                    val borderColor = when {
                        isError || isShaking -> ErrorRed
                        isFocused && otp.length < 4 -> AuraBlue
                        isFilled -> AuraPurple.copy(alpha = 0.5f)
                        else -> GlassWhite.copy(alpha = 0.2f)
                    }
                    val animatedBorderColor by animateColorAsState(targetValue = borderColor, label = "borderColor")

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .scale(scale)
                            .clip(RoundedCornerShape(16.dp))
                            .background(GlassDark)
                            .border(
                                width = if (isFocused && otp.length < 4) 2.dp else 1.dp,
                                color = animatedBorderColor,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = char,
                            transitionSpec = {
                                (fadeIn() + scaleIn(initialScale = 0.8f)) togetherWith (fadeOut() + scaleOut(targetScale = 0.8f))
                            },
                            label = "char"
                        ) { c ->
                            Text(
                                text = c,
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        
        AnimatedVisibility(visible = isError || isShaking) {
            Text(
                text = "Invalid OTP. Use 1234.",
                color = ErrorRed,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

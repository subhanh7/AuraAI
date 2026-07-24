package com.example.auraai.presentation.onboarding.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auraai.theme.*
import kotlinx.coroutines.delay

@Composable
fun ValuePropStep(
    onNext: () -> Unit
) {
    val propositions = listOf(
        "Meet Aura, your personal AI companion designed to understand you.",
        "Aura evolves with your personality, becoming a true reflection of your needs.",
        "Your data is yours. Secure, private, and always under your control."
    )

    var visibleCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        propositions.forEachIndexed { index, _ ->
            delay(800)
            visibleCount = index + 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        propositions.forEachIndexed { index, prop ->
            AnimatedVisibility(
                visible = visibleCount > index,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                Text(
                    text = prop,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (index == 0) TextPrimary else TextSecondary,
                        lineHeight = 36.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        AnimatedVisibility(
            visible = visibleCount == propositions.size,
            enter = fadeIn() + expandVertically()
        ) {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(GradientPrimary),
                        shape = RoundedCornerShape(24.dp)
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    "Get Started",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

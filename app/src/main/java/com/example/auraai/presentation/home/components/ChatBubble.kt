package com.example.auraai.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auraai.domain.model.ChatMessage
import com.example.auraai.domain.model.Sender
import com.example.auraai.theme.*

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun ChatBubble(message: ChatMessage) {
    val isAura = message.sender == Sender.AURA

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val maxBubbleWidth = if (isAura) screenWidth * 0.78f else screenWidth * 0.75f

    // Entrance Animation: 0.98 -> 1.0 scale, fade-in over 200ms (no bounce)
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(message.id) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
        )
    }

    val bubbleAlpha = animationProgress.value
    val bubbleScale = 0.98f + (0.02f * animationProgress.value)

    // Glassmorphism Materials
    val bubbleShape = RoundedCornerShape(28.dp)

    val backgroundBrush = if (isAura) {
        // AI Incoming: Dark glass surface with soft top highlight reflection (88% opacity)
        Brush.verticalGradient(
            listOf(
                Color(0xFF222226).copy(alpha = 0.88f),
                Color(0xFF161619).copy(alpha = 0.88f)
            )
        )
    } else {
        // User Outgoing: Premium dark blue vertical gradient with glass transparency (92% opacity)
        Brush.verticalGradient(
            listOf(
                Color(0xFF264C8C).copy(alpha = 0.92f),
                Color(0xFF183362).copy(alpha = 0.92f)
            )
        )
    }

    val borderStroke = if (isAura) {
        // AI Incoming: 1dp subtle semi-transparent border with top light reflection
        BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.14f),
                    Color.White.copy(alpha = 0.04f)
                )
            )
        )
    } else {
        // User Outgoing: 1dp subtle blue highlight border
        BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(
                    Color(0xFF60A5FA).copy(alpha = 0.25f),
                    Color.White.copy(alpha = 0.08f)
                )
            )
        )
    }

    val shadowSpotColor = if (isAura) {
        Color.Black.copy(alpha = 0.40f)
    } else {
        Color(0xFF3B82F6).copy(alpha = 0.30f) // Soft blue edge glow
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = bubbleAlpha
                scaleX = bubbleScale
                scaleY = bubbleScale
            },
        horizontalArrangement = if (isAura) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isAura) {
            AuraAvatar()
            Spacer(modifier = Modifier.width(12.dp))
        }

        Surface(
            shape = bubbleShape,
            color = Color.Transparent,
            border = borderStroke,
            tonalElevation = 0.dp,
            modifier = Modifier
                .widthIn(max = maxBubbleWidth)
                .shadow(
                    elevation = 6.dp,
                    shape = bubbleShape,
                    spotColor = shadowSpotColor,
                    ambientColor = Color.Black.copy(alpha = 0.2f)
                )
                .background(brush = backgroundBrush, shape = bubbleShape)
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 22.sp,
                        color = Color.White
                    )
                )
            }
        }

        if (!isAura) {
            Spacer(modifier = Modifier.width(12.dp))
            UserAvatar()
        }
    }
}

@Composable
fun AuraAvatar() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E1E20)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.SmartToy,
            contentDescription = "Aura",
            modifier = Modifier.size(20.dp),
            tint = Color.White
        )
    }
}

@Composable
fun UserAvatar() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E1E20)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "You",
            modifier = Modifier.size(20.dp),
            tint = Color(0xFF3B82F6) // Light blue outline color for person
        )
    }
}

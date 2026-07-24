package com.example.auraai.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.auraai.theme.*

@Composable
fun InputPanel(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .navigationBarsPadding(),
        shape = RoundedCornerShape(32.dp),
        color = GlassWhite.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassWhite.copy(alpha = 0.1f)),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMicClick) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = "Mic",
                    tint = TextSecondary
                )
            }

            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { 
                    Text(
                        "Message...", 
                        color = TextSecondary.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyLarge
                    ) 
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AuraBlue
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                maxLines = 4,
                minLines = 1
            )

            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (value.isNotBlank()) Brush.linearGradient(GradientPrimary) else Brush.linearGradient(listOf(GlassWhite.copy(alpha = 0.1f), GlassWhite.copy(alpha = 0.1f))))
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (value.isNotBlank()) Color.White else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

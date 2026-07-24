package com.example.auraai.presentation.onboarding.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auraai.theme.*

@Composable
fun PersonalityStep(
    selectedTraits: Set<String>,
    onTraitSelected: (String) -> Unit,
    isSaving: Boolean,
    onComplete: () -> Unit
) {
    val traits = listOf(
        "Friendly", "Curious", "Creative", "Focused",
        "Calm", "Funny", "Helpful", "Analytical"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Define Aura's Personality",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Select exactly 3 traits that will shape your companion.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, bottom = 32.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(traits) { trait ->
                TraitChip(
                    trait = trait,
                    isSelected = selectedTraits.contains(trait),
                    onClick = { onTraitSelected(trait) }
                )
            }
        }

        val isEnabled = selectedTraits.size == 3 && !isSaving

        Button(
            onClick = onComplete,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = if (isEnabled) Brush.horizontalGradient(GradientPrimary)
                            else Brush.horizontalGradient(listOf(GlassWhite, GlassWhite)),
                    shape = RoundedCornerShape(24.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    "Finish Setup",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isEnabled) Color.White else TextSecondary
                )
            }
        }
    }
}

@Composable
fun TraitChip(
    trait: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderModifier = if (isSelected) {
        Modifier.border(2.dp, Brush.horizontalGradient(GradientPrimary), RoundedCornerShape(20.dp))
    } else {
        Modifier.border(1.dp, GlassWhite.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) GlassWhite.copy(alpha = 0.1f) else GlassWhite.copy(alpha = 0.05f))
            .then(borderModifier)
            .clickable { onClick() }
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp).padding(end = 4.dp)
                )
            }
            Text(
                text = trait,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if (isSelected) Color.White else TextPrimary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}

package com.example.auraai.presentation.home
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.auraai.domain.statemachine.ConversationState
import com.example.auraai.presentation.home.components.*
import com.example.auraai.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val convState by viewModel.conversationState.collectAsState()
    val pagedMessages = viewModel.pagedMessages.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.toggleMicrophone()
            }
        }
    )

    val handleMicClick: () -> Unit = {
        if (uiState.auraState == AuraState.LISTENING) {
            viewModel.toggleMicrophone()
        } else {
            val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                viewModel.toggleMicrophone()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(pagedMessages.itemCount) {
        if (pagedMessages.itemCount > 0) {
            listState.animateScrollToItem(pagedMessages.itemCount + 1) // +2 for Header and Aura
        }
    }

    Scaffold(
        containerColor = DeepNavy,
        bottomBar = {
            InputPanel(
                value = uiState.currentInput,
                onValueChange = viewModel::onInputChange,
                onSend = viewModel::sendMessage,
                onMicClick = handleMicClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. Header Section
                item {
                    HeaderSection(userName = uiState.userName)
                }

                // 2. Aura Section
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))
                        
                        AuraCircle(
                            state = uiState.auraState,
                            amplitude = if (convState is ConversationState.Processing) 0.5f else uiState.amplitude,
                            modifier = Modifier.size(250.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Status Pill
                        StatusPill(auraState = uiState.auraState)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // 3. Chat Messages
                items(
                    count = pagedMessages.itemCount,
                    key = pagedMessages.itemKey { it.id }
                ) { index ->
                    pagedMessages[index]?.let { message ->
                        ChatBubble(message)
                    }
                }

                // Error Retry UI
                if (convState is ConversationState.Error) {
                    item {
                        val errorState = convState as ConversationState.Error
                        ErrorCard(
                            message = errorState.message,
                            onRetry = viewModel::retry
                        )
                    }
                }
            }

            // Soft atmospheric gradient overlay above input bar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                DeepNavy.copy(alpha = 0.6f),
                                DeepNavy
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun HeaderSection(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = "Good Morning,",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal,
                    color = TextPrimary
                )
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            brush = Brush.linearGradient(GradientPrimary),
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(userName.lowercase())
                    }
                },
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "How can I help you today?",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Profile Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, GlassWhite, CircleShape)
                .clip(CircleShape)
                .background(GlassDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = "Profile", tint = TextSecondary, modifier = Modifier.size(20.dp))
            // Notification dot
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-2).dp, y = 2.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AuraPurple)
            )
        }
    }
}

@Composable
fun StatusPill(auraState: AuraState) {
    val statusText = when (auraState) {
        AuraState.IDLE -> "Ready to assist"
        AuraState.LISTENING -> "Listening..."
        AuraState.PROCESSING -> "Processing..."
        AuraState.RESPONDING -> "Responding..."
    }

    Box(
        modifier = Modifier
            .height(52.dp)
            .background(Color(0xFF14141A).copy(alpha = 0.6f), RoundedCornerShape(32.dp))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = auraState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                },
                label = "status_icon"
            ) { state ->
                Box(
                    modifier = Modifier.size(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    StatusIcon(state)
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            AnimatedContent(
                targetState = statusText,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                },
                label = "status_text"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                )
            }
        }
    }
}

@Composable
fun StatusIcon(auraState: AuraState) {
    val infiniteTransition = rememberInfiniteTransition(label = "icon_anim")
    
    when (auraState) {
        AuraState.IDLE -> {
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2500, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "idle_scale"
            )
            PremiumOrb(scale = scale, coreColor = Color(0xFF7C4DFF), glowColor = Color(0xFF9333EA))
        }
        AuraState.LISTENING -> {
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "listen_scale"
            )
            PremiumOrb(scale = scale, coreColor = Color(0xFF7C4DFF), glowColor = Color(0xFF3B82F6))
        }
        AuraState.PROCESSING -> {
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "process_rot"
            )
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.98f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "process_scale"
            )
            PremiumOrb(scale = scale, rotation = rotation, coreColor = Color(0xFF9333EA), glowColor = Color(0xFF3B82F6), shimmer = true)
        }
        AuraState.RESPONDING -> {
             val scale by infiniteTransition.animateFloat(
                initialValue = 1.0f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = FastOutLinearInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "respond_scale"
            )
            PremiumOrb(scale = scale, coreColor = Color(0xFF7C4DFF), glowColor = Color(0xFFA855F7))
        }
    }
}

@Composable
fun PremiumOrb(
    scale: Float,
    coreColor: Color,
    glowColor: Color,
    rotation: Float = 0f,
    shimmer: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .scale(scale)
            .rotate(rotation),
        contentAlignment = Alignment.Center
    ) {
        // Outer soft glow (Gaussian blur effect via radial gradient)
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.7f),
                            glowColor.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        
        // Subtle edge ring (blue hint)
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF3B82F6).copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        
        // Inner intense core
        val coreBrush = if (shimmer) {
            Brush.sweepGradient(
                listOf(Color.White, coreColor, Color.White, coreColor)
            )
        } else {
            Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f),
                    coreColor,
                    coreColor.copy(alpha = 0.5f)
                )
            )
        }
        
        Box(
            modifier = Modifier
                .size(9.dp)
                .background(coreBrush, CircleShape)
        )
    }
}

@Composable
fun ErrorCard(message: String, onRetry: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = ErrorRed.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = ErrorRed,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onRetry) {
                Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = ErrorRed)
            }
        }
    }
}

package com.example.auraai.presentation.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.auraai.presentation.onboarding.components.PersonalityStep
import com.example.auraai.presentation.onboarding.components.ProfileFormStep
import com.example.auraai.presentation.onboarding.components.ValuePropStep
import com.example.auraai.theme.AuraBlue
import com.example.auraai.theme.DeepNavy
import com.example.auraai.theme.GlassWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Page Indicator
        Row(
            Modifier
                .height(48.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { iteration ->
                val color = if (pagerState.currentPage == iteration) AuraBlue else GlassWhite
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(if (pagerState.currentPage == iteration) 12.dp else 8.dp)
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false // We use the buttons for controlled flow as per validation rules
        ) { page ->
            when (page) {
                0 -> ValuePropStep(
                    onNext = {
                        scope.launch { pagerState.animateScrollToPage(1) }
                    }
                )
                1 -> ProfileFormStep(
                    uiState = uiState,
                    onNameChange = viewModel::onNameChange,
                    onAgeChange = viewModel::onAgeChange,
                    onPhoneChange = viewModel::onPhoneChange,
                    onOtpChange = viewModel::onOtpChange,
                    onNext = {
                        if (viewModel.verifyOtpAndValidateProfile()) {
                            scope.launch { pagerState.animateScrollToPage(2) }
                        }
                    }
                )
                2 -> PersonalityStep(
                    selectedTraits = uiState.selectedTraits,
                    onTraitSelected = viewModel::onTraitSelected,
                    isSaving = uiState.isSaving,
                    onComplete = {
                        viewModel.completeOnboarding(onOnboardingComplete)
                    }
                )
            }
        }
    }
}

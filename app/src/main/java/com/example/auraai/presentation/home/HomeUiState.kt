package com.example.auraai.presentation.home

import com.example.auraai.presentation.home.components.AuraState

data class HomeUiState(
    val userName: String = "",
    val auraState: AuraState = AuraState.IDLE,
    val amplitude: Float = 0f,
    val isInputPanelVisible: Boolean = false,
    val currentInput: String = ""
)

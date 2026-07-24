package com.example.auraai.domain.statemachine

import com.example.auraai.domain.model.ChatMessage

sealed class ConversationState {
    object Idle : ConversationState()
    data class Typing(val content: String) : ConversationState()
    object Validating : ConversationState()
    object Processing : ConversationState()
    data class Responding(val message: ChatMessage) : ConversationState()
    data class Error(val message: String, val lastInput: String) : ConversationState()
}

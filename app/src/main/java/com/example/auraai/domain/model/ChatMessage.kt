package com.example.auraai.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: Sender = Sender.USER,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class Sender {
    USER, AURA
}

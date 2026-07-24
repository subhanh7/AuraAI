package com.example.auraai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageMeta(
    val intent: String? = null,
    val confidence: Float = 0f,
    val attachments: List<String> = emptyList()
)

package com.example.auraai.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Reminder(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "current_user",
    val title: String,
    val triggerAt: Long,
    val isCompleted: Boolean = false
)

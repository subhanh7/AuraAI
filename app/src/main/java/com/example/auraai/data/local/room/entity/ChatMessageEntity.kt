package com.example.auraai.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.auraai.domain.model.MessageMeta

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val sender: String,
    val content: String,
    val timestamp: Long,
    val messageMeta: MessageMeta?,
    val lastSyncedAt: Long = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

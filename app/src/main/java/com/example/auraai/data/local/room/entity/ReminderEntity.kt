package com.example.auraai.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val triggerAt: Long,
    val isCompleted: Boolean,
    val lastSyncedAt: Long = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

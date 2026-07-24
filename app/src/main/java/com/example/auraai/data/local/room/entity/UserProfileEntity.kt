package com.example.auraai.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "current_user",
    val name: String,
    val age: Int,
    val phone: String,
    val traits: String, // Stored as comma-separated or JSON
    val lastSyncedAt: Long = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

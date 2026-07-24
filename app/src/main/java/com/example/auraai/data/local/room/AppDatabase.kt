package com.example.auraai.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.auraai.data.local.room.converter.MessageMetaConverter
import com.example.auraai.data.local.room.dao.ChatMessageDao
import com.example.auraai.data.local.room.dao.ReminderDao
import com.example.auraai.data.local.room.dao.UserProfileDao
import com.example.auraai.data.local.room.entity.ChatMessageEntity
import com.example.auraai.data.local.room.entity.ReminderEntity
import com.example.auraai.data.local.room.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        ChatMessageEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(MessageMetaConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun reminderDao(): ReminderDao
}

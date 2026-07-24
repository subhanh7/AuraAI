package com.example.auraai.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.auraai.data.local.room.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE userId = :userId ORDER BY triggerAt ASC")
    fun getRemindersForUser(userId: String): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE updatedAt > lastSyncedAt")
    suspend fun getDirtyReminders(): List<ReminderEntity>

    @Query("UPDATE reminders SET lastSyncedAt = :timestamp WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<String>, timestamp: Long)
}

package com.example.auraai.data.local.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.auraai.data.local.room.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getPagedMessages(): PagingSource<Int, ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("SELECT * FROM chat_messages WHERE updatedAt > lastSyncedAt")
    suspend fun getDirtyMessages(): List<ChatMessageEntity>

    @Query("UPDATE chat_messages SET lastSyncedAt = :timestamp WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<String>, timestamp: Long)
}

package com.example.auraai.domain.repository

import androidx.paging.PagingData
import com.example.auraai.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getPagedMessages(): Flow<PagingData<ChatMessage>>
    suspend fun sendMessage(content: String)
    suspend fun saveAiResponse(content: String)
}

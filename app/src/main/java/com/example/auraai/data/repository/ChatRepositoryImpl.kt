package com.example.auraai.data.repository

import androidx.paging.*
import com.example.auraai.data.local.room.dao.ChatMessageDao
import com.example.auraai.data.local.room.entity.ChatMessageEntity
import com.example.auraai.domain.model.ChatMessage
import com.example.auraai.domain.model.Sender
import com.example.auraai.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatMessageDao: ChatMessageDao
) : ChatRepository {

    override fun getPagedMessages(): Flow<PagingData<ChatMessage>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { chatMessageDao.getPagedMessages() }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                ChatMessage(
                    id = entity.id,
                    sender = Sender.valueOf(entity.sender),
                    content = entity.content,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    override suspend fun sendMessage(content: String) {
        val entity = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            sender = Sender.USER.name,
            content = content,
            timestamp = System.currentTimeMillis(),
            messageMeta = null // Can be populated for richer messages
        )
        chatMessageDao.insertMessage(entity)
    }

    override suspend fun saveAiResponse(content: String) {
        val entity = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            sender = Sender.AURA.name,
            content = content,
            timestamp = System.currentTimeMillis(),
            messageMeta = null
        )
        chatMessageDao.insertMessage(entity)
    }
}

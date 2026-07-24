package com.example.auraai.domain.repository

import com.example.auraai.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getReminders(userId: String): Flow<List<Reminder>>
    suspend fun saveReminder(reminder: Reminder)
}

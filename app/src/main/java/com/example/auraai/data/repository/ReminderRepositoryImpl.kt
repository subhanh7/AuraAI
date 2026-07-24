package com.example.auraai.data.repository

import com.example.auraai.data.local.room.dao.ReminderDao
import com.example.auraai.data.local.room.entity.ReminderEntity
import com.example.auraai.domain.model.Reminder
import com.example.auraai.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {

    override fun getReminders(userId: String): Flow<List<Reminder>> {
        return reminderDao.getRemindersForUser(userId).map { entities ->
            entities.map { entity ->
                Reminder(
                    id = entity.id,
                    userId = entity.userId,
                    title = entity.title,
                    triggerAt = entity.triggerAt,
                    isCompleted = entity.isCompleted
                )
            }
        }
    }

    override suspend fun saveReminder(reminder: Reminder) {
        val entity = ReminderEntity(
            id = reminder.id,
            userId = reminder.userId,
            title = reminder.title,
            triggerAt = reminder.triggerAt,
            isCompleted = reminder.isCompleted
        )
        reminderDao.insertReminder(entity)
    }
}

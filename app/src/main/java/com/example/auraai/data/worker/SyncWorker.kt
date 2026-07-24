package com.example.auraai.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.auraai.data.local.room.dao.ChatMessageDao
import com.example.auraai.data.local.room.dao.ReminderDao
import com.example.auraai.data.local.room.dao.UserProfileDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val userProfileDao: UserProfileDao,
    private val chatMessageDao: ChatMessageDao,
    private val reminderDao: ReminderDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val syncTimestamp = System.currentTimeMillis()

            // 1. Fetch "dirty" rows (updatedAt > lastSyncedAt)
            val dirtyProfiles = userProfileDao.getDirtyProfiles()
            val dirtyMessages = chatMessageDao.getDirtyMessages()
            val dirtyReminders = reminderDao.getDirtyReminders()

            // 2. Simulate network push to remote API
            // In a real app, we would POST these to a backend here.
            // If conflict occurred, local would "win" because we are pushing local state.
            
            // 3. Mark rows as synced
            if (dirtyProfiles.isNotEmpty()) {
                dirtyProfiles.forEach { 
                    userProfileDao.insertUserProfile(it.copy(lastSyncedAt = syncTimestamp))
                }
            }

            if (dirtyMessages.isNotEmpty()) {
                chatMessageDao.markAsSynced(dirtyMessages.map { it.id }, syncTimestamp)
            }

            if (dirtyReminders.isNotEmpty()) {
                reminderDao.markAsSynced(dirtyReminders.map { it.id }, syncTimestamp)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

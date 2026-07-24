package com.example.auraai.infrastructure.sync

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.*
import com.example.auraai.data.worker.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    init {
        // Observe sync work status
        val syncWorkName = "AuraSyncWork"
        
        CoroutineScope(Dispatchers.Main).launch {
            workManager.getWorkInfosForUniqueWorkFlow(syncWorkName).collect { workInfos ->
                val workInfo = workInfos.firstOrNull() ?: return@collect
                
                _syncStatus.value = when (workInfo.state) {
                    WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING -> SyncStatus.Syncing
                    WorkInfo.State.SUCCEEDED -> SyncStatus.Success
                    WorkInfo.State.FAILED -> SyncStatus.Error("Sync failed")
                    else -> SyncStatus.Idle
                }
            }
        }
    }

    fun triggerSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "AuraSyncWork",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    fun startPeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, java.util.concurrent.TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "AuraPeriodicSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )
    }
}

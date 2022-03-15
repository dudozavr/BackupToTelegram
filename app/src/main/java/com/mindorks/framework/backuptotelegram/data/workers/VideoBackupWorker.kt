package com.mindorks.framework.backuptotelegram.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.VideoService
import com.mindorks.framework.backuptotelegram.data.storage.media_store.BackupManager
import com.mindorks.framework.backuptotelegram.data.storage.preferences.AppPreferences
import com.mindorks.framework.backuptotelegram.data.storage.room.MediaFileRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class VideoBackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val preferences: AppPreferences,
    private val backupManager: BackupManager,
    private val videoService: VideoService,
    private val mediaFileRepository: MediaFileRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            backupManager.startVideoBackup(preferences, videoService, mediaFileRepository)
        }
        return Result.success()
    }
}

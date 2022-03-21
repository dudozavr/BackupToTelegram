package com.mindorks.framework.backuptotelegram.data.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mindorks.framework.backuptotelegram.R
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.PhotoService
import com.mindorks.framework.backuptotelegram.data.storage.media_store.BackupManager
import com.mindorks.framework.backuptotelegram.data.storage.preferences.AppPreferences
import com.mindorks.framework.backuptotelegram.data.storage.room.MediaFileRepository
import com.mindorks.framework.backuptotelegram.extentions.setForegroundWithForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

@HiltWorker
class ImageBackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val preferences: AppPreferences,
    private val backupManager: BackupManager,
    private val photoService: PhotoService,
    private val mediaFileRepository: MediaFileRepository
) : CoroutineWorker(context, workerParameters) {

    companion object {
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_ID_STRING = "ImageBackup"
    }

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            setForegroundWithForegroundInfo(
                applicationContext,
                NOTIFICATION_ID,
                NOTIFICATION_ID_STRING,
                applicationContext.getString(R.string.image_backup_notification_channel_name),
                applicationContext.getString(R.string.image_backup_notification_title),
                applicationContext.getString(R.string.backup_notification_body)
            )
            backupManager.startImageBackup(preferences, photoService, mediaFileRepository)
        }
        return Result.success()
    }
}
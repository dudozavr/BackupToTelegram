package com.mindorks.framework.backuptotelegram.data.storage.media_store

import com.mindorks.framework.backuptotelegram.data.network.telegram.services.DeleteService
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.PhotoService
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.VideoService
import com.mindorks.framework.backuptotelegram.data.storage.preferences.AppPreferences
import com.mindorks.framework.backuptotelegram.data.storage.room.MediaFileRepository

interface BackupManager {
    suspend fun startVideoBackup(
        preferences: AppPreferences,
        videoService: VideoService,
        mediaFileRepository: MediaFileRepository
    )

    suspend fun startImageBackup(
        preferences: AppPreferences,
        photoService: PhotoService,
        mediaFileRepository: MediaFileRepository
    )

    suspend fun deleteMessages(
        preferences: AppPreferences,
        deleteService: DeleteService,
        mediaFileRepository: MediaFileRepository
    )
}
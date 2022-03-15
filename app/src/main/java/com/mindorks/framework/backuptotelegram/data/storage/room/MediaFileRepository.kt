package com.mindorks.framework.backuptotelegram.data.storage.room

import com.mindorks.framework.backuptotelegram.data.storage.room.entity.MediaFile

interface MediaFileRepository {
    fun isMediaFileAlreadyReserved(mediaFileId: Long): Boolean
    fun getAllMediaFiles(): List<MediaFile>
    fun cacheMediaFile(mediaFile: MediaFile)
    fun deleteMediaFile(telegramMessageId: Int)
}
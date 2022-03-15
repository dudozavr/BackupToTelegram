package com.mindorks.framework.backuptotelegram.data.storage.room

import com.mindorks.framework.backuptotelegram.data.storage.room.entity.MediaFile

class MediaFileRepositoryImpl(private val mediaFileDao: MediaFileDao) :
    MediaFileRepository {

    override fun isMediaFileAlreadyReserved(mediaFileId: Long) =
        mediaFileDao.getMediaFile(mediaFileId).isNotEmpty()

    override fun getAllMediaFiles(): List<MediaFile> = mediaFileDao.getMediaFile()

    override fun cacheMediaFile(mediaFile: MediaFile) {
        mediaFileDao.insertMediaFile(mediaFile)
    }

    override fun deleteMediaFile(telegramMessageId: Int) {
        mediaFileDao.deleteMediaFile(telegramMessageId)
    }
}
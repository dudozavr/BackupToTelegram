package com.mindorks.framework.backuptotelegram.data.storage.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.mindorks.framework.backuptotelegram.data.storage.room.entity.MediaFile

@Dao
interface MediaFileDao {

    @Query("SELECT * FROM mediafile")
    fun getMediaFile(): List<MediaFile>

    @Insert(onConflict = REPLACE)
    fun insertMediaFile(mediaFile: MediaFile)

    @Query("SELECT * FROM mediafile WHERE id LIKE :mediaFileId")
    fun getMediaFile(mediaFileId: Long): List<MediaFile>

    @Query("DELETE FROM mediafile WHERE telegram_message_id LIKE :telegramMessageId")
    fun deleteMediaFile(telegramMessageId: Int)
}
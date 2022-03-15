package com.mindorks.framework.backuptotelegram.data.storage.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mindorks.framework.backuptotelegram.data.storage.room.entity.MediaFile

@Database(entities = [MediaFile::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getMediaFileDao(): MediaFileDao
}
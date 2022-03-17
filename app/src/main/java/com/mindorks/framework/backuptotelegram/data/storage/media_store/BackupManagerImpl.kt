package com.mindorks.framework.backuptotelegram.data.storage.media_store

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.iceteck.silicompressorr.SiliCompressor
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.DeleteService
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.PhotoService
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.VideoService
import com.mindorks.framework.backuptotelegram.data.storage.preferences.AppPreferences
import com.mindorks.framework.backuptotelegram.data.storage.room.MediaFileRepository
import com.mindorks.framework.backuptotelegram.data.storage.room.entity.MediaFile
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*
import javax.inject.Inject

class BackupManagerImpl @Inject constructor(@ApplicationContext private val context: Context) :
    BackupManager {

    companion object {
        private const val IMAGE_KEY = "photo"
        private const val VIDEO_KEY = "video"
        private const val TAG = "BackupManagerImpl"
    }

    private val imageProjection = arrayOf(
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media._ID
    )

    private val videoProjection = arrayOf(
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.DATE_TAKEN,
        MediaStore.Video.Media._ID
    )

    override suspend fun deleteMessages(
        preferences: AppPreferences,
        deleteService: DeleteService,
        mediaFileRepository: MediaFileRepository
    ) {
        mediaFileRepository.getAllMediaFiles().forEach {
            try {
                deleteService.deleteTelegramMessage(
                    apiKey = preferences.getApiKey(),
                    chat_id = preferences.getChatID(),
                    messageId = it.telegramMessageId
                )
            } catch (e: Exception) {
                println(e.message)
            }
            mediaFileRepository.deleteMediaFile(it.telegramMessageId)
        }
    }

    override suspend fun startVideoBackup(
        preferences: AppPreferences,
        videoService: VideoService,
        mediaFileRepository: MediaFileRepository
    ) {
        val cursor = getVideoCursor()
        cursor?.let {
            it.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val size = cursor.getString(sizeColumn)
                    val date = cursor.getString(dateColumn)

                    if (!mediaFileRepository.isMediaFileAlreadyReserved(id)) {
                        val contentUri =
                            ContentUris.withAppendedId(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                id
                            )

                        try {
                            val bodyRequest =
                                prepareFileToSend(contentUri, size.toInt(), name, VIDEO_KEY)

                            val response = videoService.sendVideo(
                                apiKey = preferences.getApiKey(),
                                chat_id = preferences.getChatID(),
                                video = bodyRequest
                            )
                            mediaFileRepository.cacheMediaFile(
                                MediaFile(
                                    id = id,
                                    telegramMessageId = response.result.message_id,
                                    name = name,
                                    type = VIDEO_KEY
                                )
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, e.message ?: "")
                            continue
                        }
                    }
                }
            }
        }
    }

    override suspend fun startImageBackup(
        preferences: AppPreferences,
        photoService: PhotoService,
        mediaFileRepository: MediaFileRepository
    ) {
        val cursor = getImageCursor()
        cursor?.let {
            it.use { cursor ->

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val size = cursor.getString(sizeColumn)
                    val date = cursor.getString(dateColumn)

                    if (!mediaFileRepository.isMediaFileAlreadyReserved(id)) {
                        val contentUri =
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            )

                        try {
                            val bodyRequest =
                                prepareFileToSend(contentUri, size.toInt(), name, IMAGE_KEY)
                            val response = photoService.sendPhoto(
                                apiKey = preferences.getApiKey(),
                                chat_id = preferences.getChatID(),
                                photo = bodyRequest
                            )
                            mediaFileRepository.cacheMediaFile(
                                MediaFile(
                                    id = id,
                                    telegramMessageId = response.result.message_id,
                                    name = name,
                                    type = IMAGE_KEY
                                )
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, e.message ?: "")
                            continue
                        }
                    }
                }
            }
            it.close()
        }
    }

    private fun prepareFileToSend(
        uri: Uri,
        size: Int,
        fileName: String,
        key: String
    ): MultipartBody.Part {
        val file = createTemporaryFileForBackup(uri, size, fileName, key)
        return getMultipartRequestBody(file, fileName, key)
    }

    private fun getMultipartRequestBody(file: File, name: String, key: String): MultipartBody.Part {
        val requestBody = file.asRequestBody(
            if (key == IMAGE_KEY) {
                "image/*"
            } else {
                "$VIDEO_KEY/*"
            }.toMediaTypeOrNull()
        )
        return MultipartBody.Part.createFormData(key, name, requestBody)
    }

    private fun createTemporaryFileForBackup(
        uri: Uri,
        size: Int,
        fileName: String,
        key: String
    ): File {
        val file = File(context.cacheDir, fileName).apply {
            createNewFile()
        }
        //val pathCompressedFile = compressFile(uri, key)
        val buffInput = BufferedInputStream(context.contentResolver.openInputStream(uri))
        //val buffInput = BufferedInputStream(context.contentResolver.openInputStream(Uri.parse(pathCompressedFile)))
        val bos = BufferedOutputStream(FileOutputStream(file))
        val buf = ByteArray(size)
        buffInput.read(buf)
        do {
            bos.write(buf)
        } while (buffInput.read(buf) != -1)

        return file
    }

    private fun compressFile(uri: Uri, key: String): String {
        return if (key == IMAGE_KEY) {
            SiliCompressor.with(context).compress(uri.toString(), context.cacheDir)
        } else {
            SiliCompressor.with(context).compressVideo(uri.path, context.cacheDir.absolutePath)
        }
    }

    private fun getImageCursor(): Cursor? {
        return context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            null
        )
    }

    private fun getVideoCursor(): Cursor? {
        return context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            videoProjection,
            null,
            null,
            null
        )
    }
}
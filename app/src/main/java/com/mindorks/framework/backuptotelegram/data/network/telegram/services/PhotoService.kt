package com.mindorks.framework.backuptotelegram.data.network.telegram.services

import com.mindorks.framework.backuptotelegram.data.network.telegram.model.TelegramResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface PhotoService {

    companion object {
        private const val METHOD_NAME = "sendPhoto"
        private const val API_PREFIX = "bot"
    }

    @Multipart
    @POST("/$API_PREFIX{apiKey}/{methodName}")
    suspend fun sendPhoto(
        @Path("apiKey") apiKey: String,
        @Path("methodName") methodName: String = METHOD_NAME,
        @Query("chat_id") chat_id: String,
        @Part photo: MultipartBody.Part
    ): TelegramResponse
}
package com.mindorks.framework.backuptotelegram.data.network.telegram.services

import com.mindorks.framework.backuptotelegram.data.network.telegram.model.TelegramResponse
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DeleteService {

    companion object {
        private const val METHOD_NAME = "deleteMessage"
        private const val API_PREFIX = "bot"
    }

    @POST("/${API_PREFIX}{apiKey}/{methodName}")
    suspend fun deleteTelegramMessage(
        @Path("apiKey") apiKey: String,
        @Path("methodName") methodName: String = METHOD_NAME,
        @Query("chat_id") chat_id: String,
        @Query("message_id") messageId: Int
    ): TelegramResponse
}
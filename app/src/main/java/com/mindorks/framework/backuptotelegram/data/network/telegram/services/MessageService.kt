package com.mindorks.framework.backuptotelegram.data.network.telegram.services

import com.mindorks.framework.backuptotelegram.data.network.telegram.model.TelegramResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageService {

    companion object {
        private const val METHOD_NAME = "sendMessage"
        private const val API_PREFIX = "bot"
    }

    @GET("/$API_PREFIX{apiKey}/{methodName}")
    suspend fun sendTextMessage(
        @Path("apiKey") apiKey: String,
        @Path("methodName") methodName: String = METHOD_NAME,
        @Query("chat_id") chat_id: String,
        @Query("text") text: String
    ): TelegramResponse
}
package com.mindorks.framework.backuptotelegram.data.network.telegram.model

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("message_id") val message_id: Int,
    @SerializedName("sender_chat") val sender_chat: SenderChat,
    @SerializedName("chat") val chat: Chat,
    @SerializedName("date") val date: Int,
    @SerializedName("text") val text: String
)
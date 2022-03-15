package com.mindorks.framework.backuptotelegram.data.network.telegram.model

import com.google.gson.annotations.SerializedName

data class TelegramResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("result") val result: Result
)
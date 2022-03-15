package com.mindorks.framework.backuptotelegram.data.network.telegram.model

import com.google.gson.annotations.SerializedName

data class SenderChat (
	@SerializedName("id") val id : Long,
	@SerializedName("title") val title : String,
	@SerializedName("username") val username : String,
	@SerializedName("type") val type : String
)
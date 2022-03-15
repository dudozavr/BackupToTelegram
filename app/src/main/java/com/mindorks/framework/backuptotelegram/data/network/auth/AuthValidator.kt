package com.mindorks.framework.backuptotelegram.data.network.auth

interface AuthValidator {

    fun isCredentialsValid(APIKey: String, channelID: String): Boolean
}
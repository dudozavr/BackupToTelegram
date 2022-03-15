package com.mindorks.framework.backuptotelegram.data.network.auth

import javax.inject.Inject

class AuthValidatorImpl @Inject constructor() : AuthValidator {

    override fun isCredentialsValid(APIKey: String, channelID: String) =
        isBotAPIKeyValid(APIKey) && isChannelIDValid(channelID)

    private fun isBotAPIKeyValid(APIKey: String) = APIKey.isNotBlank()

    private fun isChannelIDValid(channelID: String) = channelID.isNotBlank()
}
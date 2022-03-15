package com.mindorks.framework.backuptotelegram.data.storage.preferences

interface AppPreferences {

    fun getApiKey(): String

    fun saveApiKey(apiKey: String)

    fun getChatID(): String

    fun saveChatID(chatID: String)

    fun saveCredentials(apiKey: String, chatID: String)

    fun isCredentialsExist(): Boolean

    fun savePhotoSwitcherState(state: Boolean)

    fun saveVideoSwitcherState(state: Boolean)

    fun getPhotoSwitcherState(): Boolean

    fun getVideoSwitcherState(): Boolean
}
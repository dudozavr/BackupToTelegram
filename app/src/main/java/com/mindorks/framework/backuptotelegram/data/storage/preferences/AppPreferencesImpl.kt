package com.mindorks.framework.backuptotelegram.data.storage.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(@ApplicationContext context: Context) :
    AppPreferences {

    companion object {
        private const val PREFERENCES_NAME = "AppPreferences"
        private const val PREFERENCES_API_KEY = "PREFERENCES_API_KEY"
        private const val PREFERENCES_CHAT_ID = "PREFERENCES_CHAT_ID"
        private const val PREFERENCES_PHOTO_SWITCHER_STATE = "PREFERENCES_PHOTO_SWITCHER_STATE"
        private const val PREFERENCES_VIDEO_SWITCHER_STATE = "PREFERENCES_VIDEO_SWITCHER_STATE"
    }

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getApiKey(): String {
        return preferences.getString(PREFERENCES_API_KEY, "") ?: ""
    }

    override fun saveApiKey(apiKey: String) {
        preferences.edit().putString(PREFERENCES_API_KEY, apiKey).apply()
    }

    override fun getChatID(): String {
        return preferences.getString(PREFERENCES_CHAT_ID, "") ?: ""
    }

    override fun saveChatID(chatID: String) {
        preferences.edit().putString(PREFERENCES_CHAT_ID, chatID).apply()
    }

    override fun saveCredentials(apiKey: String, chatID: String) {
        saveApiKey(apiKey)
        saveChatID(chatID)
    }

    override fun isCredentialsExist(): Boolean {
        return getApiKey().isNotBlank() && getChatID().isNotBlank()
    }

    override fun savePhotoSwitcherState(state: Boolean) {
        preferences.edit().putBoolean(PREFERENCES_PHOTO_SWITCHER_STATE, state).apply()
    }

    override fun saveVideoSwitcherState(state: Boolean) {
        preferences.edit().putBoolean(PREFERENCES_VIDEO_SWITCHER_STATE, state).apply()
    }

    override fun getPhotoSwitcherState(): Boolean {
        return preferences.getBoolean(PREFERENCES_PHOTO_SWITCHER_STATE, false)
    }

    override fun getVideoSwitcherState(): Boolean {
        return preferences.getBoolean(PREFERENCES_VIDEO_SWITCHER_STATE, false)
    }
}
package com.mindorks.framework.backuptotelegram.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindorks.framework.backuptotelegram.data.network.auth.AuthValidator
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.MessageService
import com.mindorks.framework.backuptotelegram.data.storage.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import com.mindorks.framework.backuptotelegram.utils.prepareChatId

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val telegramMessageService: MessageService,
    private val authValidator: AuthValidator,
    private val preferences: AppPreferences
) : ViewModel() {

    companion object {
        private const val ERROR_TAG = "AuthViewModel"
    }

    val isAuthCredentialsValidLiveData = MutableLiveData<Unit>()
    val isAuthSuccessLiveData = MutableLiveData<Boolean>()

    fun checkAuthCredentials(APIKey: String, chatID: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (authValidator.isCredentialsValid(APIKey, chatID)) {
                try {
                    val preparedChatId = prepareChatId(chatID)
                    telegramMessageService.sendTextMessage(
                        apiKey = APIKey,
                        chat_id = preparedChatId,
                        text = text
                    )
                    preferences.saveCredentials(apiKey = APIKey, chatID = preparedChatId)
                    isAuthSuccessLiveData.postValue(true)
                } catch (e: Exception) {
                    Log.e(ERROR_TAG, e.message ?: "")
                    isAuthSuccessLiveData.postValue(false)
                }
            } else {
                isAuthCredentialsValidLiveData.postValue(Unit)
            }
        }
    }
}
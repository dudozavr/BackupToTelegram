package com.mindorks.framework.backuptotelegram.ui.main_fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindorks.framework.backuptotelegram.data.storage.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {
    val isUserAlreadyLogged = MutableLiveData<Boolean>()

    fun checkAuthorization() {
        isUserAlreadyLogged.postValue(appPreferences.isCredentialsExist())
    }
}
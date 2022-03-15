package com.mindorks.framework.backuptotelegram.ui.settings.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.DeleteService
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.PhotoService
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.VideoService
import com.mindorks.framework.backuptotelegram.data.storage.media_store.BackupManager
import com.mindorks.framework.backuptotelegram.data.storage.preferences.AppPreferences
import com.mindorks.framework.backuptotelegram.data.storage.room.MediaFileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupSettingsViewModel @Inject constructor(
    private val backupManager: BackupManager,
    private val photoService: PhotoService,
    private val videoService: VideoService,
    private val deleteService: DeleteService,
    private val preferences: AppPreferences,
    private val mediaFileRepository: MediaFileRepository
) : ViewModel() {

    val photoSwitcherStateLiveData = MutableLiveData<Boolean>()
    val videoSwitcherStateLiveData = MutableLiveData<Boolean>()

    fun startBackup(photoSwitcherState: Boolean, videoSwitcherState: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            backupManager.run {
                if (photoSwitcherState) {
                    startImageBackup(preferences, photoService, mediaFileRepository)
                }
                if (videoSwitcherState) {
                    startVideoBackup(preferences, videoService, mediaFileRepository)
                }
            }
        }
    }

    fun deleteMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            backupManager.deleteMessages(preferences, deleteService, mediaFileRepository)
        }
    }

    fun saveSwitchersStates(videoSwitcherState: Boolean, photoSwitcherState: Boolean) {
        preferences.savePhotoSwitcherState(photoSwitcherState)
        preferences.saveVideoSwitcherState(videoSwitcherState)
    }

    fun getSwitcherStates() {
        photoSwitcherStateLiveData.postValue(preferences.getPhotoSwitcherState())
        videoSwitcherStateLiveData.postValue(preferences.getVideoSwitcherState())
    }
}
package com.mindorks.framework.backuptotelegram.ui.settings.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.DeleteService
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.PhotoService
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.VideoService
import com.mindorks.framework.backuptotelegram.data.storage.media_store.BackupManager
import com.mindorks.framework.backuptotelegram.data.storage.preferences.AppPreferences
import com.mindorks.framework.backuptotelegram.data.storage.room.MediaFileRepository
import com.mindorks.framework.backuptotelegram.data.workers.DeleteBackupWorker
import com.mindorks.framework.backuptotelegram.data.workers.ImageBackupWorker
import com.mindorks.framework.backuptotelegram.data.workers.VideoBackupWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupSettingsViewModel @Inject constructor(
    private val backupManager: BackupManager,
    private val preferences: AppPreferences,
    private val workManager: WorkManager
) : ViewModel() {

    val photoSwitcherStateLiveData = MutableLiveData<Boolean>()
    val videoSwitcherStateLiveData = MutableLiveData<Boolean>()

    fun startBackup(photoSwitcherState: Boolean, videoSwitcherState: Boolean) {
        backupManager.run {
            if (photoSwitcherState) {
                createWorker(ImageBackupWorker::class.java)
            }
            if (videoSwitcherState) {
                createWorker(VideoBackupWorker::class.java)
            }
        }
    }

    fun deleteMessages() {
        createWorker(DeleteBackupWorker::class.java)
    }

    fun saveSwitchersStates(videoSwitcherState: Boolean, photoSwitcherState: Boolean) {
        preferences.savePhotoSwitcherState(photoSwitcherState)
        preferences.saveVideoSwitcherState(videoSwitcherState)
    }

    fun getSwitcherStates() {
        photoSwitcherStateLiveData.postValue(preferences.getPhotoSwitcherState())
        videoSwitcherStateLiveData.postValue(preferences.getVideoSwitcherState())
    }

    private fun createWorker(workerClass: Class<out ListenableWorker>) {
        workManager.beginUniqueWork(
            workerClass.name,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            OneTimeWorkRequest.from(workerClass)
        ).enqueue()
    }
}
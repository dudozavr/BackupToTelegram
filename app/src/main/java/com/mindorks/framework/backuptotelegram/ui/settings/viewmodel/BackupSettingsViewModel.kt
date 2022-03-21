package com.mindorks.framework.backuptotelegram.ui.settings.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.mindorks.framework.backuptotelegram.data.storage.media_store.BackupManager
import com.mindorks.framework.backuptotelegram.data.storage.preferences.AppPreferences
import com.mindorks.framework.backuptotelegram.data.workers.DeleteBackupWorker
import com.mindorks.framework.backuptotelegram.data.workers.ImageBackupWorker
import com.mindorks.framework.backuptotelegram.data.workers.VideoBackupWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BackupSettingsViewModel @Inject constructor(
    private val backupManager: BackupManager,
    private val preferences: AppPreferences,
    private val workManager: WorkManager
) : ViewModel() {

    val photoSwitcherStateLiveData = MutableLiveData<Boolean>()
    val videoSwitcherStateLiveData = MutableLiveData<Boolean>()
    val imageBackupComplete = MutableLiveData<Unit>()
    val videoBackupComplete = MutableLiveData<Unit>()

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

    fun getWorkInfoLiveData(workerClass: Class<out ListenableWorker>) =
        workManager.getWorkInfosForUniqueWorkLiveData(workerClass.name)

    fun isBackupWorkersExist() = workManager.run {
        val imageWorksInfo = getWorkInfosForUniqueWork(ImageBackupWorker::class.java.name).get()
        val videoWorksInfo = getWorkInfosForUniqueWork(VideoBackupWorker::class.java.name).get()
        (imageWorksInfo.size > 0 && isWorkerInProgress(imageWorksInfo.first()))
                || (videoWorksInfo.size > 0 && isWorkerInProgress(videoWorksInfo.first()))
    }

    fun isDeleteWorkersExist() = workManager.run {
        val deleteWorkInfo = getWorkInfosForUniqueWork(DeleteBackupWorker::class.java.name).get()
        deleteWorkInfo.size > 0 && isWorkerInProgress(deleteWorkInfo.first())
    }

    private fun isWorkerInProgress(workerInfo: WorkInfo) = workerInfo.run {
        state != WorkInfo.State.SUCCEEDED
                && state != WorkInfo.State.FAILED
                && state != WorkInfo.State.CANCELLED
    }

    private fun createWorker(workerClass: Class<out ListenableWorker>) {
        workManager.beginUniqueWork(
            workerClass.name,
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequest.from(workerClass)
        ).enqueue()
    }
}
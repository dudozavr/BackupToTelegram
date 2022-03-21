package com.mindorks.framework.backuptotelegram.ui.settings.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.WorkInfo
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.switchmaterial.SwitchMaterial
import com.mindorks.framework.backuptotelegram.R
import com.mindorks.framework.backuptotelegram.data.workers.DeleteBackupWorker
import com.mindorks.framework.backuptotelegram.data.workers.ImageBackupWorker
import com.mindorks.framework.backuptotelegram.ui.settings.viewmodel.BackupSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupSettingsFragment : Fragment() {

    private val backupSettingsViewModel by viewModels<BackupSettingsViewModel>()
    private lateinit var startSyncButton: AppCompatButton
    private lateinit var deleteText: AppCompatTextView
    private lateinit var photoSwither: SwitchMaterial
    private lateinit var videoSwither: SwitchMaterial
    private lateinit var backupProgressBar: LinearProgressIndicator
    private lateinit var backupGroupOfViews: Group

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_backup_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initFields()
        initListeners()
        setExtraViewAppearance()
        checkToExistWorkers()
    }

    override fun onStop() {
        backupSettingsViewModel.saveSwitchersStates(videoSwither.isChecked, photoSwither.isChecked)
        super.onStop()
    }
    
    private fun initFields() {
        view?.let {
            startSyncButton = it.findViewById(R.id.start_sync)
            deleteText = it.findViewById(R.id.delete_button)
            photoSwither = it.findViewById(R.id.photo_switcher)
            videoSwither = it.findViewById(R.id.video_switcher)
            backupProgressBar = it.findViewById(R.id.backup_progress_bar)
            backupGroupOfViews = it.findViewById(R.id.settings_group)
            backupSettingsViewModel.getSwitcherStates()
        }
    }

    private fun initListeners() {
        startSyncButton.setOnClickListener {
            setViewsVisibility(false)
            backupSettingsViewModel.startBackup(photoSwither.isChecked, videoSwither.isChecked)
            initBackupWorkersObservers()
        }
        deleteText.setOnClickListener {
            setViewsVisibility(false)
            backupSettingsViewModel.deleteMessages()
            initDeleteWorkerObservers()
        }
    }

    private fun checkToExistWorkers() {
        if (backupSettingsViewModel.isBackupWorkersExist()) {
            setViewsVisibility(false)
            initBackupWorkersObservers()
        }
        if (backupSettingsViewModel.isDeleteWorkersExist()) {
            setViewsVisibility(false)
            initDeleteWorkerObservers()
        }
    }

    private fun initBackupWorkersObservers() {
        backupSettingsViewModel.getWorkInfoLiveData(ImageBackupWorker::class.java)
            .observe(viewLifecycleOwner) {
                if (it.first().state == WorkInfo.State.SUCCEEDED) {
                    setViewsVisibility(true)
                }
            }
    }

    private fun initDeleteWorkerObservers() {
        backupSettingsViewModel.getWorkInfoLiveData(DeleteBackupWorker::class.java)
            .observe(viewLifecycleOwner) {
                if (it.first().state == WorkInfo.State.SUCCEEDED) {
                    setViewsVisibility(true)
                }
            }
    }

    private fun setExtraViewAppearance() {
        deleteText.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    private fun initObservers() {
        backupSettingsViewModel.apply {
            photoSwitcherStateLiveData.observe(viewLifecycleOwner, { state ->
                photoSwither.isChecked = state
            })
            videoSwitcherStateLiveData.observe(viewLifecycleOwner, { state ->
                videoSwither.isChecked = state
            })
        }
    }

    private fun setViewsVisibility(flag: Boolean) {
        backupGroupOfViews.isVisible = flag
        backupProgressBar.apply {
            if (!flag) {
                show()
            } else {
                hide()
            }
        }
    }
}
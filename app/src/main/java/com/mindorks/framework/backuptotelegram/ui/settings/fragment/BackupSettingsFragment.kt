package com.mindorks.framework.backuptotelegram.ui.settings.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.switchmaterial.SwitchMaterial
import com.mindorks.framework.backuptotelegram.R
import com.mindorks.framework.backuptotelegram.ui.settings.viewmodel.BackupSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupSettingsFragment : Fragment() {

    companion object {
        const val TAG = "BackupSettingsFragment"
    }

    private val backupSettingsViewModel by viewModels<BackupSettingsViewModel>()
    private lateinit var startSyncButton: AppCompatButton
    private lateinit var deleteText: AppCompatTextView
    private lateinit var photoSwither: SwitchMaterial
    private lateinit var videoSwither: SwitchMaterial

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
    }

    private fun initFields() {
        view?.let {
            startSyncButton = it.findViewById(R.id.start_sync)
            deleteText = it.findViewById(R.id.delete_button)
            photoSwither = it.findViewById(R.id.photo_switcher)
            videoSwither = it.findViewById(R.id.video_switcher)
            backupSettingsViewModel.getSwitcherStates()
        }
    }

    private fun initListeners() {
        startSyncButton.setOnClickListener {
            backupSettingsViewModel.startBackup(photoSwither.isChecked, videoSwither.isChecked)
        }
        deleteText.setOnClickListener {
            backupSettingsViewModel.deleteMessages()
        }
    }

    private fun setExtraViewAppearance() {
        deleteText.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    private fun initObservers() {
        backupSettingsViewModel.photoSwitcherStateLiveData.observe(viewLifecycleOwner, { state ->
            photoSwither.isChecked = state
        })
        backupSettingsViewModel.videoSwitcherStateLiveData.observe(viewLifecycleOwner, { state ->
            videoSwither.isChecked = state
        })
    }

    override fun onDestroy() {
        backupSettingsViewModel.saveSwitchersStates(photoSwither.isChecked, videoSwither.isChecked)
        super.onDestroy()
    }
}
package com.mindorks.framework.backuptotelegram.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.mindorks.framework.backuptotelegram.MainActivity
import com.mindorks.framework.backuptotelegram.R
import com.mindorks.framework.backuptotelegram.ui.auth.viewmodel.AuthViewModel
import com.mindorks.framework.backuptotelegram.ui.settings.fragment.BackupSettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {

    companion object {
        const val TAG = "AuthFragment"
    }

    private val authViewModel by viewModels<AuthViewModel>()
    private lateinit var apiKey: TextInputEditText
    private lateinit var chatID: TextInputEditText
    private lateinit var signUpButton: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFields()
        initListeners()
        initObservers()
    }

    private fun initFields() {
        view?.let {
            apiKey = it.findViewById(R.id.api_key)
            chatID = it.findViewById(R.id.channel_id)
            signUpButton = it.findViewById(R.id.sign_up)
        }
    }

    private fun initListeners() {
        signUpButton.setOnClickListener {
            authViewModel.checkAuthCredentials(
                apiKey.text.toString(),
                chatID.text.toString(),
                resources.getString(R.string.text_for_telegram_authorization)
            )
        }
    }

    private fun initObservers() {
        authViewModel.apply {
            isAuthCredentialsValidLiveData.observe(viewLifecycleOwner, {
                showErrorMessage(resources.getString(R.string.text_for_invalid_credentials))
            })
            isAuthSuccessLiveData.observe(viewLifecycleOwner, { isSuccess ->
                if (isSuccess) {
                    (activity as MainActivity).openFragment(
                        BackupSettingsFragment(),
                        BackupSettingsFragment.TAG,
                        true
                    )
                } else {
                    showErrorMessage(resources.getString(R.string.text_for_failed_authorization))
                }
            })
        }
    }

    private fun showErrorMessage(text: String) {
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_LONG
        ).show()
    }
}
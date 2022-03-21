package com.mindorks.framework.backuptotelegram.ui.main_fragment.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mindorks.framework.backuptotelegram.R
import com.mindorks.framework.backuptotelegram.ui.main_fragment.viewmodel.MainFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val mainFragmentViewModel by viewModels<MainFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        mainFragmentViewModel.checkAuthorization()
    }

    private fun initObservers() {
        mainFragmentViewModel.isUserAlreadyLogged.observe(viewLifecycleOwner, {isLogged ->
            if (isLogged) {
                findNavController().navigate(R.id.action_main_fragment_to_backup_settings_fragment)
            } else {
                findNavController().navigate(R.id.action_main_fragment_to_auth_fragment)
            }
        })
    }
}
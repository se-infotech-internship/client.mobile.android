package com.example.finedriver.ui.main.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.finedriver.R
import kotlinx.android.synthetic.main.fragment_main_menu.*
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        message_settings_layout.setOnClickListener(messageSettingsClickListener)
        camera_settings_layout.setOnClickListener(cameraSettingsClickListener)
        fines_settings_layout.setOnClickListener(finesSettingsClickListener)
        fines_list_back_button.setOnClickListener(backClickListener)

    }


    private val messageSettingsClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_settingsFragment2_to_messageSettingsFragment)
    }
    private val cameraSettingsClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_settingsFragment2_to_cameraSettingsFragment)
    }
    private val finesSettingsClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_settingsFragment2_to_finesSettingsFragment2)
    }
    private val backClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_settingsFragment2_to_mainMenyFragment);
    }

}
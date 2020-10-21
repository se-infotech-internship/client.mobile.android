package com.example.finedriver.ui.main.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.finedriver.R
import kotlinx.android.synthetic.main.fragment_fines_settings.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.fines_list_back_button

class FinesSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fines_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inspection_frequency_layout.setOnClickListener(finesSettingsClickListener)
        fines_list_back_button.setOnClickListener(backClickListener)

    }

    private val finesSettingsClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_finesSettingsFragment_to_inspectionSettingsFragment)
    }
    private val backClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_finesSettingsFragment_to_settingsFragment2);
    }
}
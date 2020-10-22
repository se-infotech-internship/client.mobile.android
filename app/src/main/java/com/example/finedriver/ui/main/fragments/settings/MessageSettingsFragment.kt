package com.example.finedriver.ui.main.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.finedriver.R
import kotlinx.android.synthetic.main.fragment_settings.*


class MessageSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_message_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fines_list_back_button.setOnClickListener(backClickListener)

    }


    private val backClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_messageSettingsFragment_to_settingsFragment2);
    }

}
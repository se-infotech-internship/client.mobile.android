package com.example.finedriver.ui.main.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.finedriver.R
import kotlinx.android.synthetic.main.fragment_camera_settings.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.fines_list_back_button


class CameraSettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        plus_button.setOnClickListener(plusClickListener)
        minus_button.setOnClickListener(minusClickListener)
        fines_list_back_button.setOnClickListener(backClickListener)

    }

    private val plusClickListener: View.OnClickListener = View.OnClickListener { view ->
        if (warning_radius_meter.text == "100 м") {
            warning_radius_meter.text = "200 м"
        }
        else if (warning_radius_meter.text == "200 м"){
            warning_radius_meter.text = "700 м"
        }
    }
    private val minusClickListener: View.OnClickListener = View.OnClickListener { view ->
        if (warning_radius_meter.text == "700 м") {
            warning_radius_meter.text = "200 м"
        }
        else if (warning_radius_meter.text == "200 м"){
            warning_radius_meter.text = "100 м"
        }
    }
    private val backClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_cameraSettingsFragment_to_settingsFragment2);
    }

}
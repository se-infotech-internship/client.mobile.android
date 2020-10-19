package com.example.finedriver.ui.main.fragments

import android.os.Bundle
import android.os.Handler
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finedriver.R
import kotlinx.android.synthetic.main.fragment_main_menu.*

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class MainMenuFragment : Fragment(R.layout.fragment_main_menu) {

    private val mapClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_mainMenyFragment_to_mapsFragment)
    }
    private val finesClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_mainMenyFragment_to_finesFragment);
    }
    private val cameraClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_mainMenyFragment_to_cameraListFragment);
    }
    private val profileClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_mainMenyFragment_to_userProfileFragment);
    }
    private val settingsClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_mainMenyFragment_to_settingsFragment);
    }
    private val exitClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_mainMenyFragment_to_exitFragment);
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mapLayout.setOnClickListener(mapClickListener)
        finesLayout.setOnClickListener(finesClickListener)
        cameraLayout.setOnClickListener(cameraClickListener)
        profileLayout.setOnClickListener(profileClickListener)
        settingsLayout.setOnClickListener(settingsClickListener)
        exitLayout.setOnClickListener(exitClickListener)
    }

    }
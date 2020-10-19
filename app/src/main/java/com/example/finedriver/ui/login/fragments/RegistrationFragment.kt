package com.example.finedriver.ui.login.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.finedriver.R

class RegistrationFragment : Fragment(R.layout.fragment_registration) {
    private val registrAndLoginClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_registrationFragment_to_mainMenuActivity)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registrAndLoginButton = view.findViewById<Button>(R.id.registrAndLoginButton)

        registrAndLoginButton?.setOnClickListener(registrAndLoginClickListener)

    }
}
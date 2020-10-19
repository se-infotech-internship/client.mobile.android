package com.example.finedriver.ui.login.fragments

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.example.finedriver.R


class LoginFragment : Fragment(R.layout.fragment_login) {

    private val loginClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_loginFragment_to_mainMenuActivity)
    }

    private val registrationClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_loginFragment_to_registrationFragment);
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = view.findViewById<Button>(R.id.loginButton)
       /* val registrationButton = view.findViewById<Button>(R.id.registrationButton)*/

        loginButton?.setOnClickListener(loginClickListener)
     /*   registrationButton.setOnClickListener(registrationClickListener)*/
    }

}
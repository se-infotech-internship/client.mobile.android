package com.example.finedriver.ui.login

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.finedriver.R
import com.example.finedriver.ui.login.fragments.LoginFragment

class LoginActivity : AppCompatActivity() {

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        navController = Navigation.findNavController(this, R.id.login_nav_host);

    }



}
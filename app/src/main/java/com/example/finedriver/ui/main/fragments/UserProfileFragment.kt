package com.example.finedriver.ui.main.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.finedriver.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.fragment_main_menu.image_icon_profile
import kotlinx.android.synthetic.main.fragment_user_profile.*


class UserProfileFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener {
    private var googleApiClient: GoogleApiClient? = null

    override fun onStart() {
        super.onStart()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        if(googleApiClient == null || !googleApiClient!!.isConnected()) {
            googleApiClient = GoogleApiClient.Builder(requireContext())
                .enableAutoManage(requireActivity(),1, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        }
        val opr =
            Auth.GoogleSignInApi.silentSignIn(googleApiClient)
        if (opr.isDone) {
            val result = opr.get()
            handleSignInResult(result)
        } else {
            opr.setResultCallback { googleSignInResult -> handleSignInResult(googleSignInResult) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onStop() {
        super.onStop()
        if (googleApiClient != null && googleApiClient!!.isConnected()) {
            googleApiClient?.stopAutoManage(requireActivity())
            googleApiClient?.disconnect()
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            val account = result.signInAccount
            profile_fullname.setText(account!!.displayName)
            profile_email.setText(account!!.email)


            try {
                Glide.with(this).load(account?.photoUrl).into(image_icon_profile)
            } catch (e: NullPointerException) {
                Toast.makeText(requireContext(), "image not found", Toast.LENGTH_LONG).show()
            }
        } /*else {
            gotoLoginActivity()
        }*/
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }


}
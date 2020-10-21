package com.example.finedriver.ui.main.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.finedriver.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.fragment_main_menu.*

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class MainMenuFragment : Fragment(R.layout.fragment_main_menu), GoogleApiClient.OnConnectionFailedListener {

    //private var googleApiClient: GoogleApiClient? = null

    override fun onStart() {
        super.onStart()
/*        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleApiClient = GoogleApiClient.Builder(requireContext())
            .enableAutoManage(requireActivity(), this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        exitLayout.setOnClickListener(View.OnClickListener {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback { status ->
                if (status.isSuccess) {
                    gotoLoginActivity()
                }
            }
        })


        val opr =
            Auth.GoogleSignInApi.silentSignIn(googleApiClient)
        if (opr.isDone) {
            val result = opr.get()
            handleSignInResult(result)
        } else {
            opr.setResultCallback { googleSignInResult -> handleSignInResult(googleSignInResult) }
        }*/
    }


/*    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            val account = result.signInAccount

            try {
                Glide.with(this).load(account?.photoUrl).into(image_icon_profile)
            } catch (e: NullPointerException) {
                Toast.makeText(requireContext(), "image not found", Toast.LENGTH_LONG).show()
            }
        } else {
            gotoLoginActivity()
        }
    }

    private fun gotoLoginActivity() {
        findNavController().navigate(R.id.action_mainMenyFragment_to_loginActivity)
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mapLayout.setOnClickListener(mapClickListener)
        finesLayout.setOnClickListener(finesClickListener)
        cameraLayout.setOnClickListener(cameraClickListener)
        profileLayout.setOnClickListener(profileClickListener)
        settingsLayout.setOnClickListener(settingsClickListener)
        exitLayout.setOnClickListener(exitClickListener)
    }


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
        findNavController().navigate(R.id.action_mainMenyFragment_to_settingsFragment2);
    }
    private val exitClickListener: View.OnClickListener = View.OnClickListener { view ->
        findNavController().navigate(R.id.action_mainMenyFragment_to_loginActivity)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

}

package com.example.finedriver.ui.main

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.*
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.finedriver.BeepHelper
import com.example.finedriver.R
import com.example.finedriver.data.cameraData.CameraRepository
import com.example.finedriver.data.cameraData.model.CameraItem
import com.example.finedriver.servise.LocationUpdateService
import com.example.finedriver.ui.main.fragments.map.MapUtils
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlin.math.roundToInt

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class MainMenuActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var mService: LocationUpdateService? = null
    private var mBound = false
    private lateinit var mServiceConnection: ServiceConnection
    private var myReceiver: MyReceiver? = null
    private var userSpeed : String = "0 км/г"
    private var cameraRepository = CameraRepository()
    private lateinit var camerasList : List<CameraItem>
    private var beepHelper: BeepHelper? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        myReceiver = MyReceiver()
        camerasList = cameraRepository.getCamerasList(cameraRepository.getStringFromJsonFile(this))

        if (MapUtils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions()
            }
        }

        mServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = service as LocationUpdateService.LocalBinder
                mService = binder.service
                mBound = true
            }

            override fun onServiceDisconnected(name: ComponentName) {
                mService = null
                mBound = false
            }
        }

        bindService(
            Intent(this, LocationUpdateService::class.java), mServiceConnection,
            Context.BIND_AUTO_CREATE)
    }

    override fun onStart() {
        super.onStart()
        PreferenceManager.getDefaultSharedPreferences(this)
        .registerOnSharedPreferenceChangeListener(this)
        startService(Intent(applicationContext, LocationUpdateService::class.java))
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver!!,
                IntentFilter(LocationUpdateService.ACTION_BROADCAST))
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver!!)
        super.onPause()
    }

    override fun onStop() {
        if (mBound) {

            unbindService(mServiceConnection)
            mBound = false
        }
         PreferenceManager.getDefaultSharedPreferences(this)
                 .unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.ACCESS_FINE_LOCATION)

        if (!shouldProvideRationale) {
            ActivityCompat.requestPermissions(this@MainMenuActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }


    private inner class MyReceiver : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(LocationUpdateService.EXTRA_LOCATION)
            if (location != null) {
                userSpeed = (location.getSpeed()*3.6).roundToInt().toString() + " км/г"
                speedTextView.text = userSpeed



                for (cameraItem in camerasList) {
                    val endPoint = Location("Camera")
                    endPoint.latitude = cameraItem.lat
                    endPoint.longitude = cameraItem.lon
                    var distance = location.distanceTo(endPoint).toDouble()



                    when {
                        distance <= 200 -> {
                            beepHelper = BeepHelper()
                            beepHelper!!.beep(100)
                        }
                        distance <= 400 -> {
                            beepHelper = BeepHelper()
                            beepHelper!!.beep(500)
                        }
                        distance <= 700 -> {
                            if (beepHelper == null) {
                                beepHelper = BeepHelper()
                                beepHelper!!.beep(1000)
                            }
                            Toast.makeText(
                                this@MainMenuActivity,
                                "(" + location.latitude + ", " + location.longitude + ")" + distance,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            beepHelper=null
                        }
                    }
                }
            }
        }
    }




    companion object {
        private val TAG = MainMenuActivity::class.java.simpleName
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34


    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {

    }

}
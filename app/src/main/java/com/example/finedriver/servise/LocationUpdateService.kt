package com.example.finedriver.servise

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.finedriver.BeepHelper
import com.example.finedriver.R
import com.example.finedriver.data.cameraData.CameraRepository
import com.example.finedriver.data.cameraData.model.CameraItem
import com.example.finedriver.ui.main.fragments.map.MapUtils
import com.example.finedriver.ui.main.fragments.map.MapUtils.setRequestingLocationUpdates
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlin.math.roundToInt


class LocationUpdateService : Service() {
    private val mBinder: IBinder = LocalBinder()

    private var mServiceHandler: Handler? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mNotificationManager: NotificationManager? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocation: Location? = null
    //private  var notificationManager:NotificationManagerCompat? = null


    private var cameraRepository = CameraRepository()
    private lateinit var camerasList : List<CameraItem>
    private var beepHelper: BeepHelper? = null



    override fun onCreate() {

        camerasList = cameraRepository.getCamerasList(cameraRepository.getStringFromJsonFile(this))


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult.lastLocation)
            }
        }


        //notificationManager = NotificationManagerCompat.from(this)
        getLastLocation()
        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager!!.createNotificationChannel(mChannel)
        }
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createLocationRequest()
        requestLocationUpdates()
        val startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
            false)

        if (startedFromNotification) {
            removeLocationUpdates()
            stopSelf()
        }
        return Service.START_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    override fun onBind(intent: Intent?): IBinder? {
        stopForeground(true)
        mChangingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        stopForeground(true)
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (!mChangingConfiguration && MapUtils.requestingLocationUpdates(this)) {
            startForeground(NOTIFICATION_ID, getNotification())
        }
        return true
    }

    override fun onDestroy() {
        mServiceHandler!!.removeCallbacksAndMessages(null)
    }

    inner class LocalBinder : Binder() {
        val service: LocationUpdateService
            get() = this@LocationUpdateService
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVAL
        mLocationRequest!!.fastestInterval = SHORT_UPDATE_INTERVAL
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun onNewLocation(location: Location) {
        mLocation = location

        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, location)
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent)

        if (serviceIsRunningInForeground(this)) {
            mNotificationManager!!.notify(NOTIFICATION_ID, getNotification())
        }
    }


    private fun getNotification(): Notification? {
        val intent = Intent(this, LocationUpdateService::class.java)
        val coordinationText: CharSequence = mLocation?.longitude.toString() + " " + mLocation?.latitude.toString()

        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)

        val servicePendingIntent = PendingIntent.getService(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)










        //val remoteViews = RemoteViews(packageName, R.layout.notification)
        /*remoteViews.setTextViewText(R.id.address_text, "Custom notification text")*/
        /*remoteViews.setOnClickPendingIntent(R.id.root, rootPendingIntent)*/


        /*
       Пригодится для кнопки перезапуска
       val activityPendingIntent = PendingIntent.getActivity(this, 0,
            Intent(this, MainMenuActivity::class.java), 0)*/

        val builder = NotificationCompat.Builder(this)
            /*.setSmallIcon(R.drawable.ic_camera)
            .setCustomBigContentView(remoteViews)*/
            .setColor(ContextCompat.getColor(this, R.color.pink))
            .addAction(
                R.drawable.ic_exit, getString(R.string.remove_location_updates),
                servicePendingIntent)
            .setContentText(coordinationText)
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_camera)
            .setTicker(coordinationText)
            .setWhen(System.currentTimeMillis())
        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID) // Channel ID
        }
        return builder.build()
    }





    private fun getLastLocation() {
        try {
            mFusedLocationClient!!.lastLocation
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLocation = task.result
                    } else {
                        Log.w(TAG, "Failed to get location.")
                    }
                }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission.$unlikely")
        }
    }

    fun serviceIsRunningInForeground(context: Context): Boolean {
        val manager = context.getSystemService(
            Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (javaClass.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }

    fun requestLocationUpdates() {
        setRequestingLocationUpdates(this, true)
      /*  startService(Intent(applicationContext, LocationUpdateService::class.java))*/
        try {
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest,
                mLocationCallback, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            setRequestingLocationUpdates(this, false)
        }
    }

    fun removeLocationUpdates() {
        try {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
            MapUtils.setRequestingLocationUpdates(this, false)
            stopSelf()
        } catch (unlikely: SecurityException) {
            MapUtils.setRequestingLocationUpdates(this, true)
        }
    }


    companion object {
        private val UPDATE_INTERVAL: Long = 4000
        private val  SHORT_UPDATE_INTERVAL = UPDATE_INTERVAL / 2

        private val TAG = LocationUpdateService::class.java.simpleName
        private val PACKAGE_NAME = "com.example.finedriver.servise"
        private val EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".started_from_notification"
        val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"
        val EXTRA_LOCATION = "$PACKAGE_NAME.location"
        private val NOTIFICATION_ID = 54321
        private var mChangingConfiguration = false
        private val CHANNEL_ID = "channel_01"

    }
}

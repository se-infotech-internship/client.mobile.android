package com.example.finedriver.servise

import android.app.*
import android.content.Intent
import android.content.Context
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.finedriver.ui.main.fragments.map.MapUtils
import com.example.finedriver.R
import com.example.finedriver.ui.main.MainMenuActivity
import com.example.finedriver.ui.main.fragments.map.MapUtils.setRequestingLocationUpdates
import com.example.finedriver.ui.main.fragments.map.MapsFragment
import com.google.android.gms.location.*

class LocationUpdateService : Service() {
    private val mBinder: IBinder = LocalBinder()

    private var mServiceHandler: Handler? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mNotificationManager: NotificationManager? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocation: Location? = null



    override fun onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult.lastLocation)
            }
        }
        createLocationRequest()
        getLastLocation()
        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            // Create the channel for the notification
            val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager!!.createNotificationChannel(mChannel)
        }
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service started")
        val startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
            false)

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates()
            stopSelf()
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return Service.START_NOT_STICKY
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

    class LocalBinder : Binder() {
        val service: LocationUpdateService
            get() = service
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVAL
        mLocationRequest!!.fastestInterval = SHORT_UPDATE_INTERVAL
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun onNewLocation(location: Location) {
        Log.i(TAG, "New location: $location")
        mLocation = location

        // Notify anyone listening for broadcasts about the new location.
        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, location)
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent)

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager!!.notify(NOTIFICATION_ID, getNotification())
        }
    }


    private fun getNotification(): Notification? {
        val intent = Intent(this, LocationUpdateService::class.java)
        val text: CharSequence = mLocation?.longitude.toString() + " " + mLocation?.latitude.toString()

        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        val servicePendingIntent = PendingIntent.getService(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        // The PendingIntent to launch activity.
        val activityPendingIntent = PendingIntent.getActivity(this, 0,
            Intent(this, MainMenuActivity::class.java), 0)
        val builder = NotificationCompat.Builder(this)
            .addAction(
                R.drawable.ic_exit, getString(R.string.remove_location_updates),
                servicePendingIntent)
            .setContentText(text)
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_camera)
            .setTicker(text)
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
        Log.i(TAG, "Requesting location updates")
        setRequestingLocationUpdates(this, true)
        startService(Intent(applicationContext, LocationUpdateService::class.java))
        try {
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest,
                mLocationCallback, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            setRequestingLocationUpdates(this, false)
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }
    }

    fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")
        try {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
            MapUtils.setRequestingLocationUpdates(this, false)
            stopSelf()
        } catch (unlikely: SecurityException) {
            MapUtils.setRequestingLocationUpdates(this, true)
            Log.e(TAG, "Lost location permission. Could not remove updates. $unlikely")
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

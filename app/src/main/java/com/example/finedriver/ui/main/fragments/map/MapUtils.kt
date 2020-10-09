package com.example.finedriver.ui.main.fragments.map

import android.content.Context
import android.preference.PreferenceManager

object MapUtils {
    const val KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates"

    @JvmStatic
    fun requestingLocationUpdates(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)
    }

    @JvmStatic
    fun setRequestingLocationUpdates(context: Context?, requestingLocationUpdates: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
            .apply()
    }
}
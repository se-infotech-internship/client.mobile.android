package com.example.finedriver.data.cameraData

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import com.example.finedriver.R
import com.example.finedriver.data.cameraData.model.CameraItem
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import java.io.IOException
import java.io.InputStream


class CameraRepository {
    fun getCamerasList(jsonString:String): List<CameraItem>{
        val gson = GsonBuilder().setPrettyPrinting().create()

        return gson.fromJson(jsonString , Array<CameraItem>::class.java).toMutableList()
    }

    fun getStringFromJsonFile(activity: Activity):String {
        return activity.resources.openRawResource(R.raw.cameras_list)
            .bufferedReader().use { it.readText() }
    }

    fun getStringFromJsonFile(context: Context):String {
        return context.resources.openRawResource(R.raw.cameras_list)
            .bufferedReader().use { it.readText() }
    }

    fun getCamerasCoordinationList(camerasList :List<CameraItem>) : List<LatLng>{
        var camerasCoordinationList = mutableListOf<LatLng>()

        for (cameraItem in camerasList) {
            camerasCoordinationList.add(LatLng(cameraItem.lat,cameraItem.lon))
        }
        return camerasCoordinationList
    }
}
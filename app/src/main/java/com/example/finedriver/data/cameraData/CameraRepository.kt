package com.example.finedriver.data.cameraData

import android.app.Activity
import com.example.finedriver.R
import com.example.finedriver.data.cameraData.model.CameraItem
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder

class CameraRepository {
    fun getCamerasList(jsonString:String): List<CameraItem>{
        val gson = GsonBuilder().setPrettyPrinting().create()

        return gson.fromJson(jsonString , Array<CameraItem>::class.java).toMutableList()
    }

    fun getStringFromJsonFile(activity: Activity):String {
        return activity.resources.openRawResource(R.raw.cameras_list)
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
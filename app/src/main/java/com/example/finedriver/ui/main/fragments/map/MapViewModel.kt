package com.example.finedriver.ui.main.fragments.map

import android.app.Activity
import android.graphics.Camera
import androidx.lifecycle.ViewModel
import com.example.finedriver.data.CameraRepository
import com.example.finedriver.data.model.CameraItem
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {
    private val repository = CameraRepository()

    private fun getStringFromJsonFile(activity: Activity):String {
        return repository.getStringFromJsonFile(activity)
    }

    private fun parseJsonString(Json:String):List<CameraItem>{
        return repository.getCamerasList(Json)
    }

    fun getCameraList(activity: Activity):List<CameraItem>{
        var jsonString = getStringFromJsonFile(activity)
        return parseJsonString(jsonString)
    }

    fun getCamerasCoordinationList(activity: Activity): List<LatLng>{
        return repository.getCamerasCoordinationList(getCameraList(activity))
    }


}
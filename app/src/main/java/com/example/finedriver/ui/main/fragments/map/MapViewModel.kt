package com.example.finedriver.ui.main.fragments.map

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.finedriver.data.cameraData.CameraRepository
import com.example.finedriver.data.cameraData.model.CameraItem
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {
    private val repository =
        CameraRepository()

    private fun getStringFromJsonFile(context: Context):String {
        return repository.getStringFromJsonFile(context)
    }

    private fun parseJsonString(Json:String):List<CameraItem>{
        return repository.getCamerasList(Json)
    }

    fun getCameraList(context: Context):List<CameraItem>{
        val jsonString = getStringFromJsonFile(context)
        return parseJsonString(jsonString)
    }

    fun getCamerasCoordinationList(activity: Activity): List<LatLng>{
        return repository.getCamerasCoordinationList(getCameraList(activity))
    }




}
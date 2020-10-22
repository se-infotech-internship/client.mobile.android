package com.example.finedriver.ui.main.fragments.fines

import android.content.Context
import com.example.finedriver.data.cameraData.CameraRepository
import com.example.finedriver.data.cameraData.model.CameraItem
import com.example.finedriver.data.finesData.FinesRepository
import com.example.finedriver.data.finesData.model.FinesItem

class FinesViewModel {
    private val repository =
        FinesRepository()

    private fun getStringFromJsonFile(context: Context):String {
        return repository.getStringFromJsonFile(context)
    }

    private fun parseJsonString(Json:String):List<FinesItem>{
        return repository.getFinesList(Json)
    }

    fun getFinesList(context: Context):List<FinesItem>{
        val jsonString = getStringFromJsonFile(context)
        return parseJsonString(jsonString)
    }
}
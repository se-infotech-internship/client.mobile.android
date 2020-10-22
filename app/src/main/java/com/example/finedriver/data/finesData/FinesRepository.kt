package com.example.finedriver.data.finesData

import android.app.Activity
import android.content.Context
import com.example.finedriver.R
import com.example.finedriver.data.cameraData.model.CameraItem
import com.example.finedriver.data.finesData.model.FinesItem
import com.google.gson.GsonBuilder

class FinesRepository {
    fun getFinesList(jsonString:String): List<FinesItem>{
        val gson = GsonBuilder().setPrettyPrinting().create()

        return gson.fromJson(jsonString , Array<FinesItem>::class.java).toMutableList()
    }

    fun getStringFromJsonFile(context: Context):String {
        return context.resources.openRawResource(R.raw.fees)
            .bufferedReader().use { it.readText() }
    }
}
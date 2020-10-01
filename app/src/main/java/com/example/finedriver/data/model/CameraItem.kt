package com.example.finedriver.data.model


import com.google.gson.annotations.SerializedName

data class CameraItem(
    val address: String,
    val dir: String,
    val lat: Double,
    val lon: Double,
    val speed: Int,
    val state: String
)
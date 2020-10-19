package com.example.finedriver.data.cameraData.model


data class CameraItem(
    val address: String,
    val dir: String,
    val lat: Double,
    val lon: Double,
    val speed: Int,
    val state: String
)
package com.example.finedriver.data.finesData.model


import com.google.gson.annotations.SerializedName

data class FinesItem(
    val confirmed: Boolean,
    val cost: Int,
    val createdAt: String,
    val date: String,
    val dateApplyed: Any,
    val datePayed: Any,
    val dateTransfered: Any,
    val dateUnpayed: String,
    val driverLicence: String,
    val id: String,
    val numberOf: String,
    val payed: Boolean,
    val serial: String,
    val title: String,
    val transfered: Any,
    val tzCertificate: String,
    val tzNumber: String,
    val updatedAt: String,
    val userId: String
)
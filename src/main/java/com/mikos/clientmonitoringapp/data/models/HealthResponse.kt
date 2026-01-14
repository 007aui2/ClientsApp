package com.mikos.clientmonitoringapp.data.models

import com.google.gson.annotations.SerializedName

data class HealthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("message") val message: String
)
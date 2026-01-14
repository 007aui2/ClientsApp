package com.mikos.clientmonitoringapp.data.models

import com.google.gson.annotations.SerializedName

data class Service(
    @SerializedName("id") val id: Int,
    @SerializedName("service_name") val serviceName: String,
    @SerializedName("description") val description: String? = null
)
package com.mikos.clientmonitoringapp.data.models

import com.google.gson.annotations.SerializedName

data class CreateClientRequest(
    @SerializedName("client_name") val clientName: String
)
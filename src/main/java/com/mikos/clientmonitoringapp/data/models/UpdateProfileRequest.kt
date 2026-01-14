package com.mikos.clientmonitoringapp.data.models

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("email") val email: String? = null,
    @SerializedName("full_name") val fullName: String? = null
)
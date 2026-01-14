// UpdateClientRequest.kt
package com.mikos.clientmonitoringapp.data.models

import com.google.gson.annotations.SerializedName

data class UpdateClientRequest(
    @SerializedName("planned_date")
    val planned_date: String? = null,

    @SerializedName("is_completed")
    val is_completed: Boolean? = null,

    @SerializedName("is_lurv_sent")
    val is_lurv_sent: Boolean? = null,

    // ДОБАВЬТЕ ЭТИ ПОЛЯ:
    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("notes")
    val notes: String? = null,

    @SerializedName("services")
    val services: List<Int>? = null
)
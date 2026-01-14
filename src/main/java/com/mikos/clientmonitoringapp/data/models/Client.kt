package com.mikos.clientmonitoringapp.data.models

import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("id")
    val id: Int,

    @SerializedName("client_name")
    val clientName: String,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("planned_date")
    val plannedDate: String?,

    @SerializedName("previous_month_date")
    val previousMonthDate: String?,

    @SerializedName("is_completed")
    val isCompleted: Boolean,

    @SerializedName("is_lurv_sent")
    val isLurvSent: Boolean,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("notes")
    val notes: String? = null,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("services")
    val services: List<String> = emptyList(),

    @SerializedName("service_ids")
    val serviceIds: List<Int> = emptyList()
) {
    // Безопасные геттеры
    fun getSafeClientName(): String = clientName.ifEmpty { "Клиент #$id" }
    fun getSafePlannedDate(): String = plannedDate ?: "Не назначена"
    fun getSafePreviousMonthDate(): String = previousMonthDate ?: "Не было"
    fun getSafePhone(): String = phone ?: "Телефон не указан"
    fun getSafeEmail(): String = email ?: "Email не указан"
    fun getSafeNotes(): String = notes ?: "Заметок нет"
}
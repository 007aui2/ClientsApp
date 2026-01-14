// User.kt
package com.mikos.clientmonitoringapp.data.models

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val fullName: String,
    val role: String
)


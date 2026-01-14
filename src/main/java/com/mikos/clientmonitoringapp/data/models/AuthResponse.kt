// AuthResponse.kt
package com.mikos.clientmonitoringapp.data.models

data class AuthResponse(
    val message: String,
    val token: String,
    val user: User
)

data class LoginRequest(
    val username: String,
    val password: String
)
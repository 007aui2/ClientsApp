package com.mikos.clientmonitoringapp.data.repository

import android.content.Context
import com.mikos.clientmonitoringapp.data.api.RetrofitClient
import com.mikos.clientmonitoringapp.data.models.AuthResponse
import com.mikos.clientmonitoringapp.data.models.LoginRequest
import com.mikos.clientmonitoringapp.data.models.RegisterRequest
import com.mikos.clientmonitoringapp.data.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    private val apiService = RetrofitClient.getApiService(context)

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(username, password)
                val response = apiService.login(request)

                if (response.isSuccessful) {
                    val authResponse = response.body()!!
                    Result.success(authResponse)
                } else {
                    val error = response.errorBody()?.string() ?: "Ошибка входа"
                    Result.failure(Exception(error))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        fullName: String
    ): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = RegisterRequest(username, email, password, fullName)
                val response = apiService.register(request)

                if (response.isSuccessful) {
                    val authResponse = response.body()!!
                    Result.success(authResponse)
                } else {
                    val error = response.errorBody()?.string() ?: "Ошибка регистрации"
                    Result.failure(Exception(error))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getProfile(): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProfile()

                if (response.isSuccessful) {
                    val user = response.body()!!
                    Result.success(user)
                } else {
                    Result.failure(Exception("Ошибка получения профиля"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
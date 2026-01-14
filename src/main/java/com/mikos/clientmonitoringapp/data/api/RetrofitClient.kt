package com.mikos.clientmonitoringapp.data.api

import android.content.Context
import android.util.Log
import com.mikos.clientmonitoringapp.data.AuthManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://192.168.43.100:5000/api/"
    private var apiService: ApiService? = null

    fun getApiService(context: Context): ApiService {
        if (apiService == null) {
            // Инициализируем AuthManager
            AuthManager.initialize(context)

            // Создаем logging interceptor
            val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.d("API", message)
                }
            }).apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Interceptor для добавления токена
            val authInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val token = AuthManager.getToken(context)

                val requestBuilder = originalRequest.newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")

                if (token.isNotEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                val request = requestBuilder.build()
                chain.proceed(request)
            }

            // Interceptor для обработки ошибок
            val errorInterceptor = Interceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)

                Log.d("API", "URL: ${request.url}")
                Log.d("API", "Response code: ${response.code}")

                if (response.code == 401) {
                    AuthManager.clearAuthData(context)
                }

                response
            }

            // Создаем OkHttpClient
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .addInterceptor(errorInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            // Создаем Retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService!!
    }
}
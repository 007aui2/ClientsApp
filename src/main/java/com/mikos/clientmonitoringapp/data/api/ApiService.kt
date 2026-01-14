package com.mikos.clientmonitoringapp.data.api

import com.mikos.clientmonitoringapp.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Аутентификация
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("auth/profile")
    suspend fun getProfile(): Response<User>

    @PUT("auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<User>

    // Клиенты
    @GET("clients")
    suspend fun getClients(@Query("showCompleted") showCompleted: Boolean = true): Response<List<Client>>

    @GET("clients/{id}")
    suspend fun getClientById(@Path("id") id: Int): Response<Client>

    @POST("clients")
    suspend fun createClient(@Body request: CreateClientRequest): Response<Client>

    @PUT("clients/{id}")
    suspend fun updateClient(
        @Path("id") id: Int,
        @Body request: UpdateClientRequest
    ): Response<Client>

    @DELETE("clients/{id}")
    suspend fun deleteClient(@Path("id") id: Int): Response<Unit>

    @POST("clients/complete-month")
    suspend fun completeMonth(): Response<CompleteMonthResponse>

    // Сервисы
    @GET("services")
    suspend fun getAllServices(): Response<List<Service>>

    @GET("services/client/{clientId}")
    suspend fun getClientServices(@Path("clientId") clientId: Int): Response<List<Service>>

    @POST("services/client/{clientId}/{serviceId}")
    suspend fun addServiceToClient(
        @Path("clientId") clientId: Int,
        @Path("serviceId") serviceId: Int
    ): Response<Unit>

    @DELETE("services/client/{clientId}/{serviceId}")
    suspend fun removeServiceFromClient(
        @Path("clientId") clientId: Int,
        @Path("serviceId") serviceId: Int
    ): Response<Unit>

    // Health check
    @GET("health")
    suspend fun healthCheck(): Response<HealthResponse>
}
package com.mikos.clientmonitoringapp.data.repository

import android.content.Context
import android.util.Log
import com.mikos.clientmonitoringapp.data.api.RetrofitClient
import com.mikos.clientmonitoringapp.data.models.Client
import com.mikos.clientmonitoringapp.data.models.CreateClientRequest
import com.mikos.clientmonitoringapp.data.models.UpdateClientRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class ClientRepository(private val context: Context) {
    private val apiService = RetrofitClient.getApiService(context)

    private val TAG = "ClientRepository"

    fun getClients(showCompleted: Boolean = true): Flow<List<Client>> = flow {
        try {
            Log.d(TAG, "Запрашиваем клиентов, showCompleted=$showCompleted")
            val response = apiService.getClients(showCompleted)
            if (response.isSuccessful) {
                val clients = response.body() ?: emptyList()
                Log.d(TAG, "Получено ${clients.size} клиентов")
                emit(clients)
            } else {
                Log.e(TAG, "Ошибка при получении клиентов: ${response.code()} - ${response.message()}")
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при получении клиентов: ${e.message}", e)
            emit(emptyList())
        }
    }

    suspend fun getClientById(id: Int): Result<Client> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Запрашиваем клиента с ID=$id")
                val response = apiService.getClientById(id)
                if (response.isSuccessful) {
                    val client = response.body()!!
                    Log.d(TAG, "Клиент найден: ${client.clientName}")
                    Result.success(client)
                } else {
                    Log.e(TAG, "Клиент не найден: ${response.code()}")
                    Result.failure(Exception("Клиент не найден"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Исключение при получении клиента: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    suspend fun createClient(clientName: String): Result<Client> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Создаем клиента: $clientName")
                val request = CreateClientRequest(clientName = clientName)
                val response = apiService.createClient(request)
                if (response.isSuccessful) {
                    val client = response.body()!!
                    Log.d(TAG, "Клиент создан: ${client.clientName} (ID: ${client.id})")
                    Result.success(client)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Ошибка создания"
                    Log.e(TAG, "Ошибка создания клиента: ${response.code()} - $errorBody")
                    Result.failure(Exception(errorBody))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Исключение при создании клиента: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    // В ClientRepository.kt добавьте параметры:
    suspend fun updateClient(
        id: Int,
        plannedDate: String? = null,
        isCompleted: Boolean? = null,
        isLurvSent: Boolean? = null,
        phone: String? = null,
        email: String? = null,
        notes: String? = null,
        services: List<Int>? = null
    ): Result<Client> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Обновляем клиента ID=$id")

                val request = UpdateClientRequest(
                    planned_date = plannedDate,
                    is_completed = isCompleted,
                    is_lurv_sent = isLurvSent,
                    phone = phone,
                    email = email,
                    notes = notes,
                    services = services
                )

                val response = apiService.updateClient(id, request)

                if (response.isSuccessful) {
                    val client = response.body()!!
                    Result.success(client)
                } else {
                    Result.failure(Exception("Ошибка обновления"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteClient(id: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Удаляем клиента ID=$id")
                val response = apiService.deleteClient(id)
                if (response.isSuccessful) {
                    Log.d(TAG, "Клиент ID=$id удален")
                    Result.success(Unit)
                } else {
                    Log.e(TAG, "Ошибка удаления клиента: ${response.code()}")
                    Result.failure(Exception("Ошибка удаления"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Исключение при удалении клиента: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    suspend fun completeMonth(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Завершаем месяц")
                val response = apiService.completeMonth()
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Месяц завершен"
                    Log.d(TAG, "Месяц завершен: $message")
                    Result.success(message)
                } else {
                    Log.e(TAG, "Ошибка завершения месяца: ${response.code()}")
                    Result.failure(Exception("Ошибка завершения месяца"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Исключение при завершении месяца: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
}
package com.mikos.clientmonitoringapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikos.clientmonitoringapp.data.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ClientViewModel(private val context: Context) : ViewModel() {
    private val clientRepository = ClientRepository(context)

    private val _clients = MutableStateFlow<List<com.mikos.clientmonitoringapp.data.models.Client>>(emptyList())
    val clients: StateFlow<List<com.mikos.clientmonitoringapp.data.models.Client>> = _clients.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showCompleted = MutableStateFlow(true)
    val showCompleted: StateFlow<Boolean> = _showCompleted.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedClient = MutableStateFlow<com.mikos.clientmonitoringapp.data.models.Client?>(null)
    val selectedClient: StateFlow<com.mikos.clientmonitoringapp.data.models.Client?> = _selectedClient.asStateFlow()

    init {
        loadClients()
    }

    fun loadClients() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                clientRepository.getClients(_showCompleted.value).collectLatest { clientsList ->
                    _clients.value = clientsList
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки: ${e.message}"
            }

            _isLoading.value = false
        }
    }

    fun toggleShowCompleted() {
        _showCompleted.value = !_showCompleted.value
        loadClients()
    }

    fun createClient(clientName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = clientRepository.createClient(clientName)

            if (result.isSuccess) {
                loadClients()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Ошибка создания клиента"
            }

            _isLoading.value = false
        }
    }

    fun updateClient(
        id: Int,
        plannedDate: String? = null,
        isCompleted: Boolean? = null,
        isLurvSent: Boolean? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = clientRepository.updateClient(
                id = id,
                plannedDate = plannedDate,
                isCompleted = isCompleted,
                isLurvSent = isLurvSent
            )

            if (result.isSuccess) {
                loadClients()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Ошибка обновления"
            }

            _isLoading.value = false
        }
    }

    fun deleteClient(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = clientRepository.deleteClient(id)

            if (result.isSuccess) {
                loadClients()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Ошибка удаления"
            }

            _isLoading.value = false
        }
    }

    fun completeMonth() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = clientRepository.completeMonth()

            if (result.isSuccess) {
                loadClients()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Ошибка завершения месяца"
            }

            _isLoading.value = false
        }
    }

    fun selectClient(client: com.mikos.clientmonitoringapp.data.models.Client) {
        _selectedClient.value = client
    }

    fun clearSelectedClient() {
        _selectedClient.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
    // В ClientViewModel.kt добавьте:
    fun updateClientDetails(
        id: Int,
        phone: String? = null,
        email: String? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = clientRepository.updateClient(
                id = id,
                phone = phone,
                email = email,
                notes = notes
            )

            if (result.isSuccess) {
                loadClients()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Ошибка обновления"
            }

            _isLoading.value = false
        }
    }
}
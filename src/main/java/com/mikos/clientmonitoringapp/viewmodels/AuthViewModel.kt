package com.mikos.clientmonitoringapp.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikos.clientmonitoringapp.data.AuthManager
import com.mikos.clientmonitoringapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val context: Context) : ViewModel() {
    private val TAG = "AuthViewModel"
    private val authRepository = AuthRepository(context)

    // Sealed class для состояния входа
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val user: com.mikos.clientmonitoringapp.data.models.User) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    // Sealed class для состояния регистрации
    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        data class Success(val user: com.mikos.clientmonitoringapp.data.models.User) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        Log.d(TAG, "AuthViewModel создан")
    }

    fun login(username: String, password: String) {
        Log.d(TAG, "Вызов login: username=$username")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _loginState.value = LoginState.Loading

                Log.d(TAG, "Отправляем запрос на вход")
                val result = authRepository.login(username, password)

                if (result.isSuccess) {
                    val authResponse = result.getOrNull()!!
                    Log.d(TAG, "Вход успешен: токен получен, пользователь: ${authResponse.user.username}")
                    AuthManager.saveAuthData(authResponse.token, authResponse.user)
                    _loginState.value = LoginState.Success(authResponse.user)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Ошибка входа"
                    Log.e(TAG, "Ошибка входа: $error")
                    _errorMessage.value = error
                    _loginState.value = LoginState.Error(error)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Исключение при входе: ${e.message}", e)
                _errorMessage.value = "Ошибка сети: ${e.message}"
                _loginState.value = LoginState.Error("Ошибка сети: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(username: String, email: String, password: String, fullName: String) {
        Log.d(TAG, "Вызов register: username=$username, email=$email")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _registerState.value = RegisterState.Loading

                Log.d(TAG, "Отправляем запрос на регистрацию")
                val result = authRepository.register(username, email, password, fullName)

                if (result.isSuccess) {
                    val authResponse = result.getOrNull()!!
                    Log.d(TAG, "Регистрация успешна: пользователь создан")
                    AuthManager.saveAuthData(authResponse.token, authResponse.user)
                    _registerState.value = RegisterState.Success(authResponse.user)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Ошибка регистрации"
                    Log.e(TAG, "Ошибка регистрации: $error")
                    _errorMessage.value = error
                    _registerState.value = RegisterState.Error(error)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Исключение при регистрации: ${e.message}", e)
                _errorMessage.value = "Ошибка сети: ${e.message}"
                _registerState.value = RegisterState.Error("Ошибка сети: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        Log.d(TAG, "Выход из системы")
        AuthManager.clearAuthData(context)
        _loginState.value = LoginState.Idle
        _registerState.value = RegisterState.Idle
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun checkAuthState(): Boolean {
        return try {
            val isLoggedIn = AuthManager.isLoggedIn(context)
            Log.d(TAG, "checkAuthState: isLoggedIn=$isLoggedIn")
            isLoggedIn
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка в checkAuthState: ${e.message}", e)
            false
        }
    }

    fun getCurrentUser(): com.mikos.clientmonitoringapp.data.models.User? {
        return try {
            AuthManager.getUser(context)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка в getCurrentUser: ${e.message}", e)
            null
        }
    }
}
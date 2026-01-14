package com.mikos.clientmonitoringapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mikos.clientmonitoringapp.data.AuthManager
import com.mikos.clientmonitoringapp.ui.screens.*
import com.mikos.clientmonitoringapp.ui.theme.AppTheme
import com.mikos.clientmonitoringapp.viewmodels.AuthViewModel
import com.mikos.clientmonitoringapp.viewmodels.ClientViewModel

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Приложение запускается")

        try {
            AuthManager.initialize(this)
            Log.d(TAG, "AuthManager инициализирован")

            setContent {
                AppTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation()
                    }
                }
            }
            Log.d(TAG, "setContent выполнен успешно")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка в onCreate: ${e.message}", e)
            throw e
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Приложение стартовало")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Приложение возобновлено")
    }
}

@Composable
fun AppNavigation() {
    val TAG = "AppNavigation"
    val navController = rememberNavController()
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var permissionMessage by remember { mutableStateOf("") }

    Log.d(TAG, "AppNavigation: Начало навигации")

    val authViewModel: AuthViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                AuthViewModel(context = context.applicationContext)
            }
        }
    )

    val clientViewModel: ClientViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ClientViewModel(context = context.applicationContext)
            }
        }
    )

    LaunchedEffect(Unit) {
        try {
            Log.d(TAG, "Проверяем авторизацию")
            val isLoggedIn = authViewModel.checkAuthState()
            Log.d(TAG, "isLoggedIn = $isLoggedIn")

            if (isLoggedIn) {
                Log.d(TAG, "Пользователь авторизован, переходим на dashboard")
                navController.navigate("dashboard") {
                    popUpTo("login") { inclusive = true }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при проверке авторизации: ${e.message}", e)
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            Log.d(TAG, "Открываем экран LoginScreen")
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                authViewModel = authViewModel
            )
        }

        composable("register") {
            Log.d(TAG, "Открываем экран RegisterScreen")
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        composable("dashboard") {
            Log.d(TAG, "Открываем экран DashboardScreen")
            DashboardScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                authViewModel = authViewModel,
                clientViewModel = clientViewModel,
                onClientClick = { client ->
                    navController.navigate("client/${client.id}")
                }
            )
        }

        composable(
            route = "client/{clientId}",
            arguments = listOf(navArgument("clientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getInt("clientId") ?: 0
            val client = clientViewModel.clients.value.find { it.id == clientId }

            if (client != null) {
                ClientDetailScreen(
                    client = client,
                    onBack = { navController.popBackStack() },
                    onUpdatePlannedDate = { newDate: String? ->
                        clientViewModel.updateClient(
                            id = client.id,
                            plannedDate = newDate
                        )
                    },
                    onShowPermissionDialog = { message ->
                        permissionMessage = message
                        showPermissionDialog = true
                    },
                    onEditClient = { updatedClient ->
                        // Обновляем клиента
                        clientViewModel.updateClientDetails(
                            id = updatedClient.id,
                            phone = updatedClient.phone,
                            email = updatedClient.email,
                            notes = updatedClient.notes
                        )
                        Toast.makeText(context, "Данные обновлены", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Клиент не найден")
                }
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Требуется разрешение") },
            text = { Text(permissionMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        ContextCompat.startActivity(context, intent, null)
                        showPermissionDialog = false
                    }
                ) {
                    Text("Настройки")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}
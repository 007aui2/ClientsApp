package com.mikos.clientmonitoringapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikos.clientmonitoringapp.R
import com.mikos.clientmonitoringapp.data.models.Client
import com.mikos.clientmonitoringapp.viewmodels.AuthViewModel
import com.mikos.clientmonitoringapp.viewmodels.ClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    authViewModel: AuthViewModel,
    clientViewModel: ClientViewModel,
    onClientClick: (Client) -> Unit
) {
    val clients by clientViewModel.clients.collectAsState()
    val isLoading by clientViewModel.isLoading.collectAsState()
    val showCompleted by clientViewModel.showCompleted.collectAsState()
    val errorMessage by clientViewModel.errorMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var newClientName by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var clientToDelete by remember { mutableStateOf<Client?>(null) }

    // Загружаем клиентов при открытии экрана
    LaunchedEffect(Unit) {
        clientViewModel.loadClients()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Клиенты",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = onLogout
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_exit),
                            contentDescription = "Выйти"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Добавить клиента"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Показать ошибку если есть
            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_error),
                            contentDescription = "Ошибка",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { clientViewModel.clearError() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "Закрыть"
                            )
                        }
                    }
                }
            }

            // Панель статистики
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Статистика: Всего клиентов
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_business),
                                contentDescription = "Всего",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = clients.size.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Всего",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Статистика: Активные клиенты
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_active),
                                contentDescription = "Активные",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = clients.count { !it.isCompleted }.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Активные",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Статистика: ЛУРВ отправлен
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_email),
                                contentDescription = "ЛУРВ",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = clients.count { it.isLurvSent }.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "ЛУРВ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Панель управления
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Список клиентов (${clients.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { clientViewModel.loadClients() },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_refresh),
                                contentDescription = "Обновить",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    FilterChip(
                        selected = showCompleted,
                        onClick = { clientViewModel.toggleShowCompleted() },
                        label = { Text("Завершенные") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопка завершения месяца
            if (clients.any { it.isCompleted }) {
                OutlinedButton(
                    onClick = {
                        clientViewModel.completeMonth()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    enabled = !isLoading
                ) {
                    Text("📅 Завершить месяц")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Список клиентов
            if (clients.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_empty),
                            contentDescription = "Нет клиентов",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (showCompleted) "Клиенты не найдены" else "Нет активных клиентов",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Добавьте первого клиента",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(clients) { client ->
                        ClientCard(
                            client = client,
                            onClientClick = { onClientClick(client) },
                            onToggleCompleted = { isCompleted ->
                                clientViewModel.updateClient(
                                    id = client.id,
                                    isCompleted = isCompleted
                                )
                            },
                            onToggleLurv = { isLurvSent ->
                                clientViewModel.updateClient(
                                    id = client.id,
                                    isLurvSent = isLurvSent
                                )
                            },
                            onDelete = {
                                clientToDelete = client
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Диалог добавления клиента
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Добавить клиента") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newClientName,
                        onValueChange = { newClientName = it },
                        label = { Text("Название клиента") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newClientName.isNotBlank()) {
                            clientViewModel.createClient(newClientName)
                            newClientName = ""
                            showAddDialog = false
                        }
                    },
                    enabled = newClientName.isNotBlank() && !isLoading
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    // Диалог удаления клиента
    if (showDeleteDialog && clientToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                clientToDelete = null
            },
            title = { Text("Удаление клиента") },
            text = {
                Text(
                    text = "Вы уверены, что хотите удалить клиента \"${clientToDelete?.clientName ?: ""}\"?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clientToDelete?.let { client ->
                            clientViewModel.deleteClient(client.id)
                        }
                        showDeleteDialog = false
                        clientToDelete = null
                    },
                    enabled = !isLoading
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    clientToDelete = null
                }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun ClientCard(
    client: Client,
    onClientClick: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit,
    onToggleLurv: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClientClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (client.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок с кнопкой удаления
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = client.clientName ?: "Без названия",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    if (client.isCompleted) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_check),
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text("Выполнено", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Удалить",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Информация о дате
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Дата ИТС
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = "Дата ИТС",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "ИТС:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = client.plannedDate ?: "Не назначена",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (client.plannedDate == null) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Предыдущая дата
                if (client.previousMonthDate != null) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Предыдущая:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = client.previousMonthDate ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Статусы
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Чекбокс выполнения
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Checkbox(
                        checked = client.isCompleted,
                        onCheckedChange = onToggleCompleted,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Выполнено",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Чекбокс ЛУРВ
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Checkbox(
                        checked = client.isLurvSent,
                        onCheckedChange = onToggleLurv,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "ЛУРВ отправлен",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Сервисы
            val servicesText = client.services?.joinToString(", ") ?: ""
            if (servicesText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Сервисы:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = servicesText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
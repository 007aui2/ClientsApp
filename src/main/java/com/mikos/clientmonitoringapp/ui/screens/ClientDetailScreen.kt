package com.mikos.clientmonitoringapp.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mikos.clientmonitoringapp.data.models.Client
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    client: Client,
    onBack: () -> Unit,
    onUpdatePlannedDate: (String?) -> Unit,
    onShowPermissionDialog: (String) -> Unit,
    onEditClient: (Client) -> Unit
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editedClient by remember { mutableStateOf(client) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(client.getSafeClientName()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    // Кнопка редактирования
                    IconButton(
                        onClick = { showEditDialog = true }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                    }

                    // Кнопка создания контакта
                    if (client.phone != null) {
                        IconButton(
                            onClick = {
                                try {
                                    createContact(context, client)
                                } catch (e: Exception) {
                                    onShowPermissionDialog("Для создания контакта требуется разрешение")
                                }
                            }
                        ) {
                            Icon(Icons.Default.Contacts, contentDescription = "Создать контакт")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // Кнопка звонка
            if (client.phone != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        try {
                            makePhoneCall(context, client.phone!!)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Не удалось открыть звонок", Toast.LENGTH_SHORT).show()
                        }
                    },
                    icon = { Icon(Icons.Default.Call, contentDescription = null) },
                    text = { Text("Позвонить") },
                    modifier = Modifier.padding(bottom = 80.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Карточка основной информации
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Заголовок
                    Text(
                        text = client.getSafeClientName(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Телефон
                    if (client.phone != null) {
                        InfoRow(
                            icon = Icons.Default.Phone,
                            title = "Телефон",
                            value = client.phone!!,
                            onClick = {
                                try {
                                    makePhoneCall(context, client.phone!!)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Не удалось открыть звонок", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    } else {
                        InfoRow(
                            icon = Icons.Default.Phone,
                            title = "Телефон",
                            value = "Не указан",
                            showEditButton = true,
                            onEditClick = { showEditDialog = true }
                        )
                    }

                    // Email
                    if (client.email != null) {
                        InfoRow(
                            icon = Icons.Default.Email,
                            title = "Email",
                            value = client.email!!
                        )
                    } else {
                        InfoRow(
                            icon = Icons.Default.Email,
                            title = "Email",
                            value = "Не указан",
                            showEditButton = true,
                            onEditClick = { showEditDialog = true }
                        )
                    }

                    // Дата ИТС
                    InfoRow(
                        icon = Icons.Default.CalendarToday,
                        title = "Запланированная дата ИТС",
                        value = client.getSafePlannedDate(),
                        showEditButton = true,
                        onEditClick = { showDatePicker = true }
                    )

                    // Предыдущая дата
                    InfoRow(
                        icon = Icons.Default.History,
                        title = "Предыдущая дата ИТС",
                        value = client.getSafePreviousMonthDate()
                    )

                    // Заметки
                    if (client.notes != null && client.notes!!.isNotEmpty()) {
                        InfoRow(
                            icon = Icons.Default.Note,
                            title = "Заметки",
                            value = client.notes!!
                        )
                    }
                }
            }

            // Карточка статусов
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatusChip(
                        label = "Выполнено",
                        isActive = client.isCompleted,
                        icon = Icons.Default.CheckCircle
                    )
                    StatusChip(
                        label = "ЛУРВ отправлен",
                        isActive = client.isLurvSent,
                        icon = Icons.Default.Email
                    )
                }
            }

            // Карточка сервисов
            if (client.services.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Подключенные сервисы",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            client.services.forEach { service ->
                                FilterChip(
                                    selected = true,
                                    onClick = {},
                                    label = { Text(service) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Build,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Диалог редактирования
    if (showEditDialog) {
        EditClientDialog(
            client = editedClient,
            onSave = { updatedClient ->
                onEditClient(updatedClient)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }

    // DatePicker диалог
    if (showDatePicker) {
        CustomDatePickerDialog(
            initialDate = client.plannedDate?.let {
                try {
                    LocalDate.parse(it)
                } catch (e: Exception) {
                    LocalDate.now()
                }
            } ?: LocalDate.now(),
            onDateSelected = { date ->
                val formattedDate = date.format(DateTimeFormatter.ISO_DATE)
                onUpdatePlannedDate(formattedDate)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

// DatePicker диалог
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var year by remember { mutableStateOf(initialDate.year) }
    var month by remember { mutableStateOf(initialDate.monthValue - 1) }
    var day by remember { mutableStateOf(initialDate.dayOfMonth) }

    val daysInMonth = remember(month, year) {
        try {
            LocalDate.of(year, month + 1, 1).lengthOfMonth()
        } catch (e: Exception) {
            31
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите дату ИТС") },
        text = {
            Column {
                OutlinedTextField(
                    value = String.format("%02d.%02d.%d", day, month + 1, year),
                    onValueChange = { dateStr ->
                        try {
                            val parts = dateStr.split(".")
                            if (parts.size == 3) {
                                day = parts[0].toInt().coerceIn(1, 31)
                                month = parts[1].toInt().coerceIn(1, 12) - 1
                                year = parts[2].toInt().coerceIn(2000, 2100)
                                selectedDate = LocalDate.of(year, month + 1, day)
                            }
                        } catch (e: Exception) {}
                    },
                    label = { Text("Дата (ДД.ММ.ГГГГ)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("15.01.2024") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NumberSelector(
                        label = "Год",
                        value = year,
                        range = 2023..2030,
                        onValueChange = { year = it }
                    )
                    NumberSelector(
                        label = "Месяц",
                        value = month + 1,
                        range = 1..12,
                        onValueChange = { month = it - 1 }
                    )
                    NumberSelector(
                        label = "День",
                        value = day,
                        range = 1..daysInMonth,
                        onValueChange = { day = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            selectedDate = LocalDate.now().plusDays(7)
                            year = selectedDate.year
                            month = selectedDate.monthValue - 1
                            day = selectedDate.dayOfMonth
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text("Через неделю")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            selectedDate = LocalDate.now().plusMonths(1)
                            year = selectedDate.year
                            month = selectedDate.monthValue - 1
                            day = selectedDate.dayOfMonth
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text("Через месяц")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Выбрано: ${selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDateSelected(selectedDate)
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun NumberSelector(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    if (value > range.first) onValueChange(value - 1)
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Уменьшить")
            }

            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(
                onClick = {
                    if (value < range.last) onValueChange(value + 1)
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Увеличить")
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    showEditButton: Boolean = false,
    onClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable(enabled = onClick != null) {
                    onClick?.invoke()
                }
            )
        }
        if (showEditButton && onEditClick != null) {
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Изменить")
            }
        }
    }
}

@Composable
fun StatusChip(
    label: String,
    isActive: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = if (isActive) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// Диалог редактирования клиента
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClientDialog(
    client: Client,
    onSave: (Client) -> Unit,
    onDismiss: () -> Unit
) {
    var phone by remember { mutableStateOf(client.phone ?: "") }
    var email by remember { mutableStateOf(client.email ?: "") }
    var notes by remember { mutableStateOf(client.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать клиента") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Телефон") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("+7 999 123-45-67") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = "Телефон")
                    }
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("client@example.com") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email")
                    }
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Заметки") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Дополнительная информация...") },
                    minLines = 3,
                    leadingIcon = {
                        Icon(Icons.Default.Note, contentDescription = "Заметки")
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedClient = client.copy(
                        phone = phone.ifEmpty { null },
                        email = email.ifEmpty { null },
                        notes = notes.ifEmpty { null }
                    )
                    onSave(updatedClient)
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

// Функция для звонка
fun makePhoneCall(context: android.content.Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    ContextCompat.startActivity(context, intent, null)
}

// Функция для создания контакта
fun createContact(context: android.content.Context, client: Client) {
    val intent = Intent(Intent.ACTION_INSERT).apply {
        type = ContactsContract.Contacts.CONTENT_TYPE
        putExtra(ContactsContract.Intents.Insert.NAME, client.getSafeClientName())
        client.phone?.let {
            putExtra(ContactsContract.Intents.Insert.PHONE, it)
        }
        client.email?.let {
            putExtra(ContactsContract.Intents.Insert.EMAIL, it)
        }
        val notes = buildString {
            append("Клиент из системы мониторинга\n")
            client.plannedDate?.let {
                append("Дата ИТС: $it\n")
            }
            client.notes?.let {
                append("Заметки: $it\n")
            }
        }
        putExtra(ContactsContract.Intents.Insert.NOTES, notes)
    }
    ContextCompat.startActivity(context, intent, null)
}
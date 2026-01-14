package com.mikos.clientmonitoringapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate = LocalDate.now()
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var year by remember { mutableStateOf(initialDate.year) }
    var month by remember { mutableStateOf(initialDate.monthValue - 1) } // 0-11
    var day by remember { mutableStateOf(initialDate.dayOfMonth) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Выберите дату",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Простой DatePicker (можно заменить на готовую библиотеку)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NumberPicker(
                        label = "Год",
                        value = year,
                        range = 2023..2030,
                        onValueChange = { year = it }
                    )
                    NumberPicker(
                        label = "Месяц",
                        value = month + 1,
                        range = 1..12,
                        onValueChange = { month = it - 1 }
                    )
                    NumberPicker(
                        label = "День",
                        value = day,
                        range = 1..31,
                        onValueChange = { day = it }
                    )
                }

                // Отображение выбранной даты
                Text(
                    text = "Выбрано: ${String.format("%02d.%02d.%d", day, month + 1, year)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Отмена")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            selectedDate = LocalDate.of(year, month + 1, day)
                            onDateSelected(selectedDate)
                            onDismissRequest()
                        }
                    ) {
                        Text("Сохранить")
                    }
                }
            }
        }
    }
}

@Composable
fun NumberPicker(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        Button(
            onClick = {
                if (value < range.last) onValueChange(value + 1)
            },
            modifier = Modifier.size(48.dp)
        ) {
            Text("+")
        }
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(
            onClick = {
                if (value > range.first) onValueChange(value - 1)
            },
            modifier = Modifier.size(48.dp)
        ) {
            Text("-")
        }
    }
}
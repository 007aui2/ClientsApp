package com.mikos.clientmonitoringapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mikos.clientmonitoringapp.R

@Composable
fun PermissionDialog(
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Требуется разрешение") },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("Перейти в настройки")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
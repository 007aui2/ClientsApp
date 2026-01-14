package com.mikos.clientmonitoringapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

object PermissionUtils {
    fun hasPhonePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasContactPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasAllPermissions(context: Context): Boolean {
        return hasPhonePermission(context) && hasContactPermission(context)
    }
}

@Composable
fun rememberPermissionLauncher(
    onGranted: () -> Unit = {},
    onDenied: () -> Unit = {},
    onRationale: () -> Unit = {}
): androidx.activity.result.ActivityResultLauncher<Array<String>> {
    val context = LocalContext.current

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                onGranted()
            } else {
                val shouldShowRationale = permissions.any {
                    !it.value && androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        it.key
                    )
                }
                if (shouldShowRationale) {
                    onRationale()
                } else {
                    onDenied()
                }
            }
        }
    )
}
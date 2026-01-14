package com.mikos.clientmonitoringapp.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.mikos.clientmonitoringapp.data.models.User

object AuthManager {
    private const val PREFS_NAME = "client_monitoring_auth"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER = "auth_user"

    private lateinit var prefs: SharedPreferences

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuthData(token: String, user: User) {
        val editor = prefs.edit()
        editor.putString(KEY_TOKEN, token)
        editor.putString(KEY_USER, Gson().toJson(user))
        editor.apply()
    }

    fun getToken(context: Context): String {
        if (!::prefs.isInitialized) {
            initialize(context)
        }
        return prefs.getString(KEY_TOKEN, "") ?: ""
    }

    fun getUser(context: Context): User? {
        if (!::prefs.isInitialized) {
            initialize(context)
        }
        val userJson = prefs.getString(KEY_USER, null)
        return if (userJson != null) {
            try {
                Gson().fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    fun isLoggedIn(context: Context): Boolean {
        return getToken(context).isNotEmpty()
    }

    fun clearAuthData(context: Context) {
        if (!::prefs.isInitialized) {
            initialize(context)
        }
        val editor = prefs.edit()
        editor.remove(KEY_TOKEN)
        editor.remove(KEY_USER)
        editor.apply()
    }
}
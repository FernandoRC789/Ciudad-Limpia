package com.nickrodriguez.ciudadlimpia.utils

import android.content.Context

class SessionManager(
    private val context: Context
) {

    private val prefs =
        context.getSharedPreferences(
            "ciudad_limpia",
            Context.MODE_PRIVATE
        )

    fun saveToken(token: String) {

        prefs.edit()
            .putString("token", token)
            .apply()
    }

    fun getToken(): String? {
        return prefs.getString(
            "token",
            null
        )
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    companion object
}
package com.nickrodriguez.ciudadlimpia.utils

import android.content.Context

class SessionManager(
    private val context: Context
) {
    private val prefs =
        context.getSharedPreferences("ciudad_limpia", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)  // ← clave corregida
    }

    fun saveNombre(nombre: String) {
        prefs.edit().putString("nombre_usuario", nombre).apply()
    }

    fun getNombre(): String? {
        return prefs.getString("nombre_usuario", null)
    }

    fun getUserId(): Long {
        return prefs.getLong("usuario_id", -1L)
    }

    fun getRol(): String? {
        return prefs.getString("rol", null)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    companion object
}
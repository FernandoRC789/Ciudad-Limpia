package com.nickrodriguez.ciudadlimpia.model

data class AuthResponse(
    val token: String,
    val tipo: String,
    val rol: String,
    val usuarioId: Long
)
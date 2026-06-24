package com.nickrodriguez.ciudadlimpia.model

class RegisterUserRequest (
    val dni: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String,
    val telefono: String,
    val distrito: String,
    val direccion: String,
)
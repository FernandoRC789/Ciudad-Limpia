package com.nickrodriguez.ciudadlimpia.validation

class RegisterUserValidationErrors (
    val dni: String? = null,
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val distrito: String? = null,
    val direccion: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null
){
    fun hasErrors(): Boolean {

        return listOf(
            dni,
            nombre,
            apellido,
            email,
            telefono,
            distrito,
            direccion,
            password,
            confirmPassword
        ).any { it != null }
    }
}
package com.nickrodriguez.ciudadlimpia.validation

import java.util.Calendar

object RegisterUserValidator {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@(.+)$")
    private const val MIN_AGE = 17  // mínimo para usar la app
    private const val DNI_LENGTH = 8
    private const val MIN_PASSWORD_LENGTH = 8
    private const val PHONE_LENGTH = 9

    fun validateStep1(
        dni: String, nombre: String, apellido: String,
        email: String, telefono: String, password: String, confirmPassword: String
    ): RegisterUserValidationErrors {
        return RegisterUserValidationErrors(
            dni = when {
                dni.isBlank()           -> "El DNI es obligatorio"
                dni.length != DNI_LENGTH -> "El DNI debe tener 8 dígitos"
                !dni.all { it.isDigit() }-> "Solo se permiten números"
                else                     -> null
            },
            nombre = when {
                nombre.isBlank() -> "El nombre es obligatorio"
                nombre.length < 2 -> "Mínimo 2 caracteres"
                else             -> null
            },
            apellido = when {
                apellido.isBlank() -> "El apellido es obligatorio"
                apellido.length < 2 -> "Mínimo 2 caracteres"
                else               -> null
            },
            email = when {
                email.isBlank()                    -> "El email es obligatorio"
                !EMAIL_REGEX.matches(email)        -> "Email inválido"
                else                               -> null
            },
            telefono = when {
                telefono.isBlank()                  -> "El teléfono es obligatorio"
                telefono.length != PHONE_LENGTH     -> "Debe tener 9 dígitos (sin prefijo)"
                !telefono.all { it.isDigit() }      -> "Solo números"
                else                                -> null
            },
            /*fechaNacimiento = when {
                fechaNacimiento.isBlank() -> "La fecha de nacimiento es obligatoria"
                !isOldEnough(fechaNacimiento) -> "Debes tener al menos $MIN_AGE años"
                else -> null
            },
            genero = if (genero.isBlank()) "Selecciona una opción" else null,*/
            password = when {
                password.isBlank()                      -> "La contraseña es obligatoria"
                password.length < MIN_PASSWORD_LENGTH   -> "Mínimo $MIN_PASSWORD_LENGTH caracteres"
                !password.any { it.isDigit() }          -> "Debe incluir al menos un número"
                else                                     -> null
            },
            confirmPassword = when {
                confirmPassword.isBlank()  -> "Confirma tu contraseña"
                confirmPassword != password -> "Las contraseñas no coinciden"
                else                        -> null
            }
        )
    }

    /*fun validateStep2(
        distrito: String, direccion: String
    ): RegisterUserValidationErrors {
        return RegisterUserValidationErrors(
            distrito  = if (distrito.isBlank()) "Selecciona tu distrito" else null,
            direccion = if (direccion.isBlank()) "La dirección es obligatoria" else null
        )
    }*/

    /** Verifica que el usuario tenga al menos MIN_AGE años. */
    private fun isOldEnough(dateStr: String): Boolean {
        return try {
            val parts = dateStr.split("-")
            val birthYear  = parts[0].toInt()
            val birthMonth = parts[1].toInt()
            val birthDay   = parts[2].toInt()
            val now = Calendar.getInstance()
            val age = now.get(Calendar.YEAR) - birthYear -
                    if (now.get(Calendar.MONTH) + 1 < birthMonth ||
                        (now.get(Calendar.MONTH) + 1 == birthMonth && now.get(Calendar.DAY_OF_MONTH) < birthDay)
                    ) 1 else 0
            age >= MIN_AGE
        } catch (e: Exception) { false }
    }


}
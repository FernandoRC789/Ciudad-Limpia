package com.nickrodriguez.ciudadlimpia.validation

object Validator {

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun getEmailError(email: String): String? {
        return when {
            email.isBlank() -> "El correo no puede estar vacío"
            !isValidEmail(email) -> "Correo inválido"
            else -> null
        }
    }

    fun getPasswordError(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña no puede estar vacía"
            !isValidPassword(password) -> "Mínimo 6 caracteres"
            else -> null
        }
    }

    fun getPhoneError(phone: String): String? {
        return when {
            phone.isBlank() ->
                "El teléfono no puede estar vacío"

            !phone.matches(Regex("^[0-9]+$")) ->
                "Solo se permiten números"

            phone.length != 9 ->
                "Debe tener exactamente 9 dígitos"

            !phone.startsWith("9") ->
                "Debe empezar con 9"

            else -> null
        }
    }
}
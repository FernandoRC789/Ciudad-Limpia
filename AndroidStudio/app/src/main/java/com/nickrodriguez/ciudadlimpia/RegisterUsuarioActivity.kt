package com.nickrodriguez.ciudadlimpia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.app.DatePickerDialog
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

// ─────────────────────────────────────────────────────────────────────────────
// DATA MODELS — alineados con la API + campos nuevos
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Modelo de request de registro.
 * Extiende el JSON base con los campos necesarios para la app cívica.
 *
 * JSON BASE:
 * {
 *   "dni": "12345678",
 *   "nombre": "Juan",
 *   "apellido": "Pérez",
 *   "email": "juan@email.com",
 *   "password": "123456",
 *   "telefono": "987654321"
 * }
 *
 * CAMPOS NUEVOS SUGERIDOS (se añaden al body de la API):
 *   fechaNacimiento  → para validar mayoría de edad y personalizar
 *   genero           → para estadísticas de participación ciudadana
 *   departamento     → jerarquía geográfica
 *   provincia        → jerarquía geográfica
 *   distrito         → CRÍTICO: asigna la municipalidad responsable
 *   direccion        → dirección del ciudadano
 *   referencia       → referencia adicional (opcional)
 */
data class RegisterRequest(
    // Campos base de la API
    val dni: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String,
    val telefono: String,

    // Campos nuevos sugeridos
    val fechaNacimiento: String,    // formato: "YYYY-MM-DD"
    val genero: String,             // "M" | "F" | "ND"
    val departamento: String,
    val provincia: String,
    val distrito: String,           // clave para asignar municipalidad
    val direccion: String,
    val referencia: String? = null  // opcional
)

/** Errores de validación por campo */
data class RegisterValidationErrors(
    val dni: String? = null,
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val fechaNacimiento: String? = null,
    val genero: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,
    val distrito: String? = null,
    val direccion: String? = null
) {
    fun hasErrors() = listOf(dni, nombre, apellido, email, telefono,
        fechaNacimiento, genero, password, confirmPassword,
        distrito, direccion).any { it != null }
}

// ─────────────────────────────────────────────────────────────────────────────
// VALIDATOR — lógica de negocio separada de la UI
// ─────────────────────────────────────────────────────────────────────────────

object RegisterValidator {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@(.+)$")
    private const val MIN_AGE = 17  // mínimo para usar la app
    private const val DNI_LENGTH = 8
    private const val MIN_PASSWORD_LENGTH = 8
    private const val PHONE_LENGTH = 9

    fun validateStep1(
        dni: String, nombre: String, apellido: String,
        email: String, telefono: String, fechaNacimiento: String,
        genero: String, password: String, confirmPassword: String
    ): RegisterValidationErrors {
        return RegisterValidationErrors(
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
            fechaNacimiento = when {
                fechaNacimiento.isBlank() -> "La fecha de nacimiento es obligatoria"
                !isOldEnough(fechaNacimiento) -> "Debes tener al menos $MIN_AGE años"
                else -> null
            },
            genero = if (genero.isBlank()) "Selecciona una opción" else null,
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

    fun validateStep2(
        distrito: String, direccion: String
    ): RegisterValidationErrors {
        return RegisterValidationErrors(
            distrito  = if (distrito.isBlank()) "Selecciona tu distrito" else null,
            direccion = if (direccion.isBlank()) "La dirección es obligatoria" else null
        )
    }

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

// ─────────────────────────────────────────────────────────────────────────────
// ACTIVITY
// ─────────────────────────────────────────────────────────────────────────────

class RegisterUsuarioActivity : AppCompatActivity() {

    private var currentStep = 1
    private var selectedBirthDate = ""

    // ── Paso 1: referencias a campos ─────────────────────────────────────────
    private val tilDni             by lazy { findViewById<TextInputLayout>(R.id.tilDni) }
    private val tilNombre          by lazy { findViewById<TextInputLayout>(R.id.tilNombre) }
    private val tilApellido        by lazy { findViewById<TextInputLayout>(R.id.tilApellido) }
    private val tilEmail           by lazy { findViewById<TextInputLayout>(R.id.tilEmail) }
    private val tilTelefono        by lazy { findViewById<TextInputLayout>(R.id.tilTelefono) }
    private val tilFecha           by lazy { findViewById<TextInputLayout>(R.id.tilFechaNacimiento) }
    private val tilGenero          by lazy { findViewById<TextInputLayout>(R.id.tilGenero) }
    private val tilPassword        by lazy { findViewById<TextInputLayout>(R.id.tilPassword) }
    private val tilConfirmPassword by lazy { findViewById<TextInputLayout>(R.id.tilConfirmPassword) }

    // ── Paso 2: referencias a campos ─────────────────────────────────────────
    private val tilDistrito  by lazy { findViewById<TextInputLayout>(R.id.tilDistrito) }
    private val tilDireccion by lazy { findViewById<TextInputLayout>(R.id.tilDireccion) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_usuario)

        setupGenderDropdown()
        setupDatePicker()
        setupNextButton()
        setupLoginLink()
    }

    // ── Dropdowns ─────────────────────────────────────────────────────────────

    private fun setupGenderDropdown() {
        val opciones = resources.getStringArray(R.array.genero_opciones)
        val adapter  = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opciones)
        findViewById<AutoCompleteTextView>(R.id.actvGenero)?.setAdapter(adapter)
    }

    // ── DatePicker ────────────────────────────────────────────────────────────

    private fun setupDatePicker() {
        val etFecha = tilFecha.editText ?: return
        etFecha.setOnClickListener { showDatePicker() }
        tilFecha.setEndIconOnClickListener { showDatePicker() }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val formatted = "%04d-%02d-%02d".format(year, month + 1, day)
                selectedBirthDate = formatted
                val display = "%02d/%02d/%04d".format(day, month + 1, year)
                tilFecha.editText?.setText(display)
            },
            cal.get(Calendar.YEAR) - 18,
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).also { picker ->
            picker.datePicker.maxDate = System.currentTimeMillis()
        }.show()
    }

    // ── Navegación entre pasos ────────────────────────────────────────────────

    private fun setupNextButton() {
        findViewById<MaterialButton>(R.id.btnNext)?.setOnClickListener {
            when (currentStep) {
                1 -> validateAndGoToStep2()
                2 -> validateAndSubmit()
            }
        }
    }

    private fun validateAndGoToStep2() {
        val errors = RegisterValidator.validateStep1(
            dni              = tilDni.editText?.text.toString().trim(),
            nombre           = tilNombre.editText?.text.toString().trim(),
            apellido         = tilApellido.editText?.text.toString().trim(),
            email            = tilEmail.editText?.text.toString().trim(),
            telefono         = tilTelefono.editText?.text.toString().trim(),
            fechaNacimiento  = selectedBirthDate,
            genero           = (tilGenero.editText as? AutoCompleteTextView)?.text.toString().trim(),
            password         = tilPassword.editText?.text.toString(),
            confirmPassword  = tilConfirmPassword.editText?.text.toString()
        )

        applyStep1Errors(errors)
        /*if (!errors.hasErrors()) goToStep2()*/
    }

    private fun validateAndSubmit() {
        val errors = RegisterValidator.validateStep2(
            distrito  = (tilDistrito?.editText as? AutoCompleteTextView)?.text.toString().trim(),
            direccion = tilDireccion?.editText?.text.toString().trim()
        )

        applyStep2Errors(errors)
        if (!errors.hasErrors()) submitRegistration()
    }

    /*private fun goToStep2() {
        currentStep = 2
        setContentView(R.layout.)  // o navigate con NavController
        setupStep2()
    }*/

    private fun setupStep2() {
        setupDistritoDropdown()
        setupNextButton()
    }

    private fun setupDistritoDropdown() {
        // En producción: cargar desde API o base de datos local
        val distritos = arrayOf(
            "Miraflores", "San Isidro", "Barranco", "Surco",
            "La Molina", "Callao", "Ventanilla", "Bellavista",
            "San Miguel", "Pueblo Libre", "Jesús María", "Lince"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, distritos)
        findViewById<AutoCompleteTextView>(R.id.actvDistrito)?.setAdapter(adapter)
    }

    // ── Envío de formulario ───────────────────────────────────────────────────

    private fun submitRegistration() {
        val request = buildRegisterRequest()
        // En producción usar ViewModel + Retrofit:
        // registerViewModel.register(request)
        Toast.makeText(this, "Registro: ${request.nombre} en ${request.distrito}", Toast.LENGTH_LONG).show()
    }

    private fun buildRegisterRequest() = RegisterRequest(
        dni             = tilDni.editText?.text.toString().trim(),
        nombre          = tilNombre.editText?.text.toString().trim(),
        apellido        = tilApellido.editText?.text.toString().trim(),
        email           = tilEmail.editText?.text.toString().trim(),
        password        = tilPassword.editText?.text.toString(),
        telefono        = tilTelefono.editText?.text.toString().trim(),
        fechaNacimiento = selectedBirthDate,
        genero          = generoToCode((tilGenero.editText as? AutoCompleteTextView)?.text.toString()),
        departamento    = (findViewById<AutoCompleteTextView>(R.id.actvDepartamento))?.text.toString().trim(),
        provincia       = (findViewById<AutoCompleteTextView>(R.id.actvProvincia))?.text.toString().trim(),
        distrito        = (tilDistrito?.editText as? AutoCompleteTextView)?.text.toString().trim(),
        direccion       = tilDireccion?.editText?.text.toString().trim(),
        referencia      = findViewById<TextInputLayout>(R.id.tilReferencia)?.editText?.text.toString().trim().ifBlank { null }
    )

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun applyStep1Errors(e: RegisterValidationErrors) {
        tilDni.error             = e.dni
        tilNombre.error          = e.nombre
        tilApellido.error        = e.apellido
        tilEmail.error           = e.email
        tilTelefono.error        = e.telefono
        tilFecha.error           = e.fechaNacimiento
        tilGenero.error          = e.genero
        tilPassword.error        = e.password
        tilConfirmPassword.error = e.confirmPassword
    }

    private fun applyStep2Errors(e: RegisterValidationErrors) {
        tilDistrito?.error  = e.distrito
        tilDireccion?.error = e.direccion
    }

    private fun generoToCode(display: String) = when (display) {
        "Masculino"          -> "M"
        "Femenino"           -> "F"
        else                 -> "ND"
    }

    private fun setupLoginLink() {
        findViewById<TextView>(R.id.tvLogin)?.setOnClickListener {
            finish()  // o navegar a LoginActivity
        }
    }
}

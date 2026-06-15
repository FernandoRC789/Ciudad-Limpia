package com.nickrodriguez.ciudadlimpia.ui.auth

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.model.RegisterUserRequest
import com.nickrodriguez.ciudadlimpia.network.RetrofitClient
import com.nickrodriguez.ciudadlimpia.validation.RegisterUserValidationErrors
import com.nickrodriguez.ciudadlimpia.validation.RegisterUserValidator
import kotlinx.coroutines.launch

class RegisterUsuarioActivity : AppCompatActivity() {

    //private var currentStep = 1
    //private var selectedBirthDate = ""

    // ── Paso 1: referencias a campos ─────────────────────────────────────────
    private val tilDni             by lazy { findViewById<TextInputLayout>(R.id.tilDni) }
    private val tilNombre          by lazy { findViewById<TextInputLayout>(R.id.tilNombre) }
    private val tilApellido        by lazy { findViewById<TextInputLayout>(R.id.tilApellido) }
    private val tilEmail           by lazy { findViewById<TextInputLayout>(R.id.tilEmail) }
    private val tilTelefono        by lazy { findViewById<TextInputLayout>(R.id.tilTelefono) }
    /*private val tilFecha           by lazy { findViewById<TextInputLayout>(R.id.tilFechaNacimiento) }
    private val tilGenero          by lazy { findViewById<TextInputLayout>(R.id.tilGenero) }*/
    private val tilPassword        by lazy { findViewById<TextInputLayout>(R.id.tilPassword) }
    private val tilConfirmPassword by lazy { findViewById<TextInputLayout>(R.id.tilConfirmPassword) }

    // ── Paso 2: referencias a campos ─────────────────────────────────────────
    /*private val tilDistrito  by lazy { findViewById<TextInputLayout>(R.id.tilDistrito) }
    private val tilDireccion by lazy { findViewById<TextInputLayout>(R.id.tilDireccion) }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_usuario)

        /*setupGenderDropdown()
        setupDatePicker()
        setupNextButton()*/
        setupLoginLink()
        setupRegisterButton()
    }

    // ── Dropdowns ─────────────────────────────────────────────────────────────

    /*private fun setupGenderDropdown() {
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
        val errors = RegisterUserValidator.validateStep1(
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
        val errors = RegisterUserValidator.validateStep2(
            distrito  = (tilDistrito?.editText as? AutoCompleteTextView)?.text.toString().trim(),
            direccion = tilDireccion?.editText?.text.toString().trim()
        )
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
    }*/

    // ── Envío de formulario ───────────────────────────────────────────────────

    private fun submitRegistration() {

        val request = buildRegisterRequest()

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.apiService
                        .register(request)

                if(response.isSuccessful){

                    Toast.makeText(
                        this@RegisterUsuarioActivity,
                        "Usuario registrado",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                }else{

                    Toast.makeText(
                        this@RegisterUsuarioActivity,
                        "Error al registrar",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }catch (e: Exception){

                Toast.makeText(
                    this@RegisterUsuarioActivity,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun buildRegisterRequest() = RegisterUserRequest(
        dni = tilDni.editText?.text.toString().trim(),
        nombre = tilNombre.editText?.text.toString().trim(),
        apellido = tilApellido.editText?.text.toString().trim(),
        email = tilEmail.editText?.text.toString().trim(),
        password = tilPassword.editText?.text.toString(),
        telefono = tilTelefono.editText?.text.toString().trim(),
        /*fechaNacimiento = selectedBirthDate,
        genero          = generoToCode((tilGenero.editText as? AutoCompleteTextView)?.text.toString()),
        departamento    = (findViewById<AutoCompleteTextView>(R.id.actvDepartamento))?.text.toString().trim(),
        provincia       = (findViewById<AutoCompleteTextView>(R.id.actvProvincia))?.text.toString().trim(),
        distrito        = (tilDistrito?.editText as? AutoCompleteTextView)?.text.toString().trim(),
        direccion       = tilDireccion?.editText?.text.toString().trim(),
        referencia      = findViewById<TextInputLayout>(R.id.tilReferencia)?.editText?.text.toString().trim().ifBlank { null }*/
    )

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun applyStep1Errors(e: RegisterUserValidationErrors) {
        tilDni.error             = e.dni
        tilNombre.error          = e.nombre
        tilApellido.error        = e.apellido
        tilEmail.error           = e.email
        tilTelefono.error        = e.telefono
        //tilFecha.error           = e.fechaNacimiento
        //tilGenero.error          = e.genero
        tilPassword.error        = e.password
        tilConfirmPassword.error = e.confirmPassword
    }

    /*private fun applyStep2Errors(e: RegisterUserValidationErrors) {
        tilDistrito?.error  = e.distrito
        tilDireccion?.error = e.direccion
    }

    private fun generoToCode(display: String) = when (display) {
        "Masculino"          -> "M"
        "Femenino"           -> "F"
        else                 -> "ND"
    }*/

    private fun setupLoginLink() {
        findViewById<TextView>(R.id.tvLogin)?.setOnClickListener {
            finish()  // o navegar a LoginActivity
        }
    }

    private fun setupRegisterButton() {

        findViewById<MaterialButton>(R.id.btnNext)
            .setOnClickListener {

                validarYRegistrar()
            }
    }

    private fun validarYRegistrar() {

        val errors =
            RegisterUserValidator.validateStep1(
                dni = tilDni.editText?.text.toString().trim(),
                nombre = tilNombre.editText?.text.toString().trim(),
                apellido = tilApellido.editText?.text.toString().trim(),
                email = tilEmail.editText?.text.toString().trim(),
                telefono = tilTelefono.editText?.text.toString().trim(),
                password = tilPassword.editText?.text.toString(),
                confirmPassword =
                    tilConfirmPassword.editText?.text.toString()
            )

        applyStep1Errors(errors)

        if (!errors.hasErrors()) {

            submitRegistration()
        }
    }
}
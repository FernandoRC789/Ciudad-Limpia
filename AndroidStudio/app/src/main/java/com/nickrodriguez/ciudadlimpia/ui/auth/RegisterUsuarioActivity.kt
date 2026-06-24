package com.nickrodriguez.ciudadlimpia.ui.auth

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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

    //DECLARACION DE VARIABLES, usamos los ids de cada elemento para usar sus valores.
    // ── Paso 1: referencias a campos ─────────────────────────────────────────
    private val tilDni             by lazy { findViewById<TextInputLayout>(R.id.tilDni) }
    private val tilNombre          by lazy { findViewById<TextInputLayout>(R.id.tilNombre) }
    private val tilApellido        by lazy { findViewById<TextInputLayout>(R.id.tilApellido) }
    private val tilEmail           by lazy { findViewById<TextInputLayout>(R.id.tilEmail) }
    private val tilTelefono        by lazy { findViewById<TextInputLayout>(R.id.tilTelefono) }
    private val tilDistrito        by lazy { findViewById<TextInputLayout>(R.id.tilDistrito) }
    private val tilDirection        by lazy { findViewById<TextInputLayout>(R.id.tilDireccion) }
    private val tilPassword        by lazy { findViewById<TextInputLayout>(R.id.tilPassword) }
    private val tilConfirmPassword by lazy { findViewById<TextInputLayout>(R.id.tilConfirmPassword) }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_usuario)
        setupLoginLink()
        setupRegisterButton() // 👈 ESTO FALTA
    }

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
        dni = tilDni.text(),
        nombre = tilNombre.text(),
        apellido = tilApellido.text(),
        email = tilEmail.text(),
        telefono = tilTelefono.text(),
        direccion = tilDirection.text(),
        distrito = tilDistrito.text(),
        password = tilPassword.password()

        )

    // ── Helpers ───────────────────────────────────────────────────────────────
    private fun applyStep1Errors(e: RegisterUserValidationErrors) {
        tilDni.error             = e.dni
        tilNombre.error          = e.nombre
        tilApellido.error        = e.apellido
        tilEmail.error           = e.email
        tilTelefono.error        = e.telefono
        tilDirection.error       = e.direccion
        tilDistrito.error        = e.distrito
        tilPassword.error        = e.password
        tilConfirmPassword.error = e.confirmPassword
    }

    private fun setupLoginLink() {
        findViewById<TextView>(R.id.tvLogin)?.setOnClickListener {
            finish()  // o navegar a LoginActivity
        }
    }

    private fun setupRegisterButton() {
        findViewById<MaterialButton>(R.id.btnNextRegister).setOnClickListener {
            validateAndRegiste()
        }
    }

    private fun validateAndRegiste() {
        val errors = RegisterUserValidator.validateStep1(
            dni = tilDni.text(),
            nombre = tilNombre.text(),
            apellido = tilApellido.text(),
            email = tilEmail.text(),
            telefono = tilTelefono.text(),
            direccion = tilDirection.text(),
            distrito = tilDistrito.text(),
            password = tilPassword.password(),
            confirmPassword = tilConfirmPassword.password()
        )
        applyStep1Errors(errors)

        if (!errors.hasErrors()) {
            submitRegistration()
        }
    }

    private fun TextInputLayout.text(): String {
        return editText?.text?.toString()?.trim().orEmpty()
    }

    private fun TextInputLayout.password(): String {
        return editText?.text?.toString().orEmpty()
    }

}
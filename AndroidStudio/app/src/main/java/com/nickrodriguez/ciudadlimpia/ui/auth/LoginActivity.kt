package com.nickrodriguez.ciudadlimpia.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.nickrodriguez.ciudadlimpia.MainActivity
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.model.AuthRequest
import com.nickrodriguez.ciudadlimpia.network.RetrofitClient
import kotlinx.coroutines.launch
import android.util.Patterns
import com.nickrodriguez.ciudadlimpia.ui.admin.MapaAdminActivity
import com.nickrodriguez.ciudadlimpia.validation.Validator

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
    }

    private fun setupListeners() {

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            val emailOk = validateEmail()
            val passwordOk = validatePassword()

            if (emailOk && passwordOk) {
                login(email, password)
            } else {
                Toast.makeText(this, "Revisa los campos", Toast.LENGTH_SHORT).show()
            }        }

        tvRegister.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterUsuarioActivity::class.java
                )
            )
        }

        // 👇 VALIDACIÓN AL PERDER FOCO (UX PRO)
        etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateEmail()
        }

        etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validatePassword()
        }
    }

    private fun login(
        email:String,
        password:String
    ){
        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.apiService.login(
                        AuthRequest(
                            email,
                            password
                        )
                    )

                if(response.isSuccessful){

                    val authResponse = response.body()

                    if(authResponse != null){

                        val prefs = getSharedPreferences(
                            "ciudad_limpia",
                            MODE_PRIVATE
                        )

                        prefs.edit()
                            .putString(
                                "jwt_token",
                                authResponse.token
                            )
                            .putString(
                                "rol",
                                authResponse.rol
                            )
                            .putLong(
                                "usuario_id",
                                authResponse.usuarioId
                            )
                            .apply()

                        // Navegar según el rol
                        val destino = if (authResponse.rol.contains("ADMIN")) {
                            MapaAdminActivity::class.java
                        } else {
                            MainActivity::class.java
                        }

                        Toast.makeText(
                            this@LoginActivity,
                            "Bienvenido ${authResponse.rol}",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this@LoginActivity,
                                destino
                            )
                        )

                        finish()
                    }

                }else{

                    Toast.makeText(
                        this@LoginActivity,
                        "Credenciales incorrectas",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }catch (e:Exception){

                Toast.makeText(
                    this@LoginActivity,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun validarCampos(
        email: String,
        password: String
    ): Boolean {

        if (email.isBlank()) {

            etEmail.error = "Ingrese su correo electrónico"
            etEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            etEmail.error = "Ingrese un correo válido"
            etEmail.requestFocus()
            return false
        }

        if (password.isBlank()) {

            etPassword.error = "Ingrese su contraseña"
            etPassword.requestFocus()
            return false
        }

        if (password.length < 8) {

            etPassword.error =
                "La contraseña debe tener al menos 8 caracteres"

            etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun validateEmail(): Boolean {
        val error = Validator.getEmailError(etEmail.text.toString())
        etEmail.error = error
        return error == null
    }

    private fun validatePassword(): Boolean {
        val error = Validator.getPasswordError(etPassword.text.toString())
        etPassword.error = error
        return error == null
    }
}
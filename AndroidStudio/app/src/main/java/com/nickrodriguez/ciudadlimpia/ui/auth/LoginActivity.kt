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

            val email =
                etEmail.text.toString().trim()

            val password =
                etPassword.text.toString().trim()

            if(email.isEmpty()){

                etEmail.error = "Ingrese su email"
                return@setOnClickListener
            }

            if(password.isEmpty()){

                etPassword.error = "Ingrese su contraseña"
                return@setOnClickListener
            }

            login(email, password)
        }

        tvRegister.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterUsuarioActivity::class.java
                )
            )
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

                        Toast.makeText(
                            this@LoginActivity,
                            "Bienvenido ${authResponse.rol}",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this@LoginActivity,
                                MainActivity::class.java
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
}
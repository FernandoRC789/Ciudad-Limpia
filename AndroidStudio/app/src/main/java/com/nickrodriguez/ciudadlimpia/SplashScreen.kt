package com.nickrodriguez.ciudadlimpia

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nickrodriguez.ciudadlimpia.ui.auth.LoginActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)

        val prefs = getSharedPreferences(
            "ciudad_limpia",
            MODE_PRIVATE
        )

        val onboardingCompleted = false

        /*val onboardingCompleted =
            prefs.getBoolean(
                "onboarding_completed",
                false
            )*/


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_splashscreen)) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val logo = findViewById<ImageView>(R.id.imgLogo)
        val titulo = findViewById<TextView>(R.id.txtTitulo)
        val subtitulo = findViewById<TextView>(R.id.txtSubtitulo)

        logo.scaleX = 0.7f
        logo.scaleY = 0.7f

        logo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1200)
            .start()

        titulo.animate()
            .alpha(1f)
            .setStartDelay(600)
            .setDuration(800)
            .start()

        subtitulo.animate()
            .alpha(1f)
            .setStartDelay(900)
            .setDuration(800)
            .start()

        Handler(Looper.getMainLooper()).postDelayed({
            if(onboardingCompleted){

                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )

            }else{

                startActivity(
                    Intent(
                        this,
                        OnboardingActivity::class.java
                    )
                )
            }

            finish()

        }, 3000)
    }
}
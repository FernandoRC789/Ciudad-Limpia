package com.nickrodriguez.ciudadlimpia

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.nickrodriguez.ciudadlimpia.adapter.OnboardingAdapter
import com.nickrodriguez.ciudadlimpia.model.OnboardingItem
import androidx.core.view.WindowCompat
import com.nickrodriguez.ciudadlimpia.ui.auth.LoginActivity

class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: MaterialButton
    private lateinit var dotsLayout: LinearLayout
    private lateinit var dots: Array<TextView>
    private lateinit var txtSkip: TextView
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var pageChangeCallback:
            ViewPager2.OnPageChangeCallback

    private val onboardingItems = listOf(
        OnboardingItem(
            R.drawable.reporta_incidencias,
            null,
            "Reporta incidencias",
            "Identifica basura, desmonte y problemas urbanos de forma rápida.",
            R.color.onboarding_1_fondo,
            R.color.onboarding_1_title,
            R.color.onboarding_1_description,
            R.color.onboarding_1_boton_background,
            R.color.onboarding_1_boton_text,
            R.color.onboarding_1_omitir
        ),

        OnboardingItem(
            null,
            "maps_photo.json",
            "Adjunta evidencia",
            "Envía fotos junto con la ubicación exacta del incidente.",
            R.color.onboarding_2_fondo,
            R.color.onboarding_2_title,
            R.color.onboarding_2_description,
            R.color.onboarding_2_boton_background,
            R.color.onboarding_2_boton_text,
            R.color.onboarding_2_omitir
        ),

        OnboardingItem(
            null,
            "premios_recompensas.json",
            "Gana recompensas",
            "Obtén puntos, niveles y beneficios por ayudar a tu comunidad.",
            R.color.onboarding_3_fondo,
            R.color.onboarding_3_title,
            R.color.onboarding_3_description,
            R.color.onboarding_3_boton_background,
            R.color.onboarding_3_boton_text,
            R.color.onboarding_3_omitir        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_onboarding)
            configureStatusBar()
            configureInsets()

            initViews()
            setupViewPager()
        rootLayout.setBackgroundColor(
            ContextCompat.getColor(
                this,
                onboardingItems[0].backgroundColorRes
            )
        )
        updateTheme(0)
        updateStatusBar(0)
            setupIndicators()
            setupListeners()
    }
    private fun configureStatusBar(){
        //para que la fecha,hora,etc no se vea  blanco

        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = true
    }

    private fun updateStatusBar(position: Int) {

        val colorRes =
            onboardingItems[position].backgroundColorRes

        window.statusBarColor =
            ContextCompat.getColor(
                this,
                colorRes
            )

        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightStatusBars =
            position == 0
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        btnNext = findViewById(R.id.btnNext)
        // o 0 o
        dotsLayout = findViewById(R.id.dotsLayout)
        txtSkip = findViewById(R.id.txtSkip)
        rootLayout =
            findViewById(R.id.activity_onboarding)
    }

    private fun setupViewPager() {
        viewPager.adapter = OnboardingAdapter(onboardingItems)
    }

    private fun setupListeners() {

        btnNext.setOnClickListener {
            if (viewPager.currentItem == onboardingItems.size - 1) {
                navigateToLogin()
            } else {
                viewPager.currentItem += 1
            }
        }

        pageChangeCallback =
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {

                    setCurrentIndicator(position)

                    btnNext.text =
                        if(position == onboardingItems.size - 1)
                            "Comenzar"
                        else
                            "Siguiente"

                    rootLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            this@OnboardingActivity,
                            onboardingItems[position].backgroundColorRes
                        )
                    )

                    updateTheme(position)

                    updateStatusBar(position)
                }
            }

        viewPager.registerOnPageChangeCallback(
            pageChangeCallback
        )

        txtSkip.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun updateTheme(position: Int) {

        val item = onboardingItems[position]

        btnNext.setBackgroundColor(
            ContextCompat.getColor(
                this,
                item.buttonBackgroundRes
            )
        )

        btnNext.setTextColor(
            ContextCompat.getColor(
                this,
                item.buttonTextColorRes
            )
        )

        txtSkip.setTextColor(
            ContextCompat.getColor(
                this,
                item.skipTextColorRes
            )
        )
    }

    private fun configureInsets() {

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.activity_onboarding)
        ) { view, insets ->

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
    }

    private fun setupIndicators() {

        dots = Array(onboardingItems.size) {
            TextView(this)
        }

        for(i in dots.indices){

            dots[i].text = "○"
            dots[i].textSize = 24f
            dots[i].setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.indicator_inactive
                )
            )
            dotsLayout.addView(dots[i])
        }

        setCurrentIndicator(0)
    }

    private fun setCurrentIndicator(position: Int){

        for(i in dots.indices){

            if(i == position){

                dots[i].text = "◉"
                dots[i].textSize = 24f
                dots[i].setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.indicator_active
                    )
                )

            }else{

                dots[i].text = "○"

                dots[i].setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.indicator_inactive
                    )
                )
            }
        }
    }

    private fun saveOnboardingCompleted(){

        val prefs = getSharedPreferences(
            "ciudad_limpia",
            MODE_PRIVATE
        )

        prefs.edit()
            .putBoolean(
                "onboarding_completed",
                true
            )
            .apply()
    }

    private fun navigateToLogin() {
        saveOnboardingCompleted()
        startActivity(
            Intent(
                this,
                LoginActivity::class.java
            )
        )
        finish()
    }

    override fun onDestroy() {

        if(::pageChangeCallback.isInitialized){
            viewPager.unregisterOnPageChangeCallback(
                pageChangeCallback
            )
        }

        super.onDestroy()
    }
}

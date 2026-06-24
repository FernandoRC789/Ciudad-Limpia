package com.nickrodriguez.ciudadlimpia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nickrodriguez.ciudadlimpia.ui.auth.LoginActivity
import com.nickrodriguez.ciudadlimpia.ui.home.HomeFragment
import com.nickrodriguez.ciudadlimpia.ui.profile.ProfileFragment

/**
 * MainActivity — CORREGIDA
 *
 * CAMBIO CLAVE: en vez de crear un Fragment nuevo en cada tap
 * (lo que forzaba re-inflar el layout Y repetir las llamadas a la API),
 * ahora los 2 Fragments se crean UNA SOLA VEZ al iniciar la Activity
 * y se alternan con show()/hide(). Sus vistas y sus datos ya cargados
 * permanecen en memoria mientras navegas entre tabs.
 *
 * Resultado: cambiar de tab es instantáneo, sin pantalla en blanco,
 * sin volver a pedir datos al servidor.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    // ── Los Fragments viven aquí, una sola instancia de cada uno ──────────────
    private val homeFragment by lazy { HomeFragment() }
    private val profileFragment by lazy { ProfileFragment() }

    // Fragment actualmente visible (para no llamar show() sobre el que ya se ve)
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)

        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        bottomNav =
            findViewById(R.id.bottomNavigation)

        if(savedInstanceState == null){
            setupInitialFragments()
        }
        setupBottomNavigation()
    }

    // ── Agrega AMBOS fragments de una vez, mostrando solo Home ─────────────────
    private fun setupInitialFragments() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, profileFragment, TAG_PROFILE)
            .hide(profileFragment)
            .add(R.id.fragmentContainer, homeFragment, TAG_HOME)
            .commit()

        activeFragment = homeFragment
    }

    // ── Alterna visibilidad en vez de recrear ───────────────────────────────────
    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener {
            val targetFragment = when (it.itemId) {
                R.id.nav_feed    -> homeFragment
                R.id.nav_profile -> profileFragment
                else             -> homeFragment
            }
            switchTo(targetFragment)
            true
        }
    }

    private fun switchTo(target: Fragment) {
        if (target === activeFragment) return  // ya está visible, no hacer nada
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(target)
            .commit()
        activeFragment = target
    }

    private fun logout() {
        val prefs = getSharedPreferences("ciudad_limpia", MODE_PRIVATE)
        prefs.edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG_HOME = "fragment_home"
        private const val TAG_PROFILE = "fragment_profile"
    }
}
package com.nickrodriguez.ciudadlimpia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nickrodriguez.ciudadlimpia.ui.auth.LoginActivity
import com.nickrodriguez.ciudadlimpia.ui.home.HomeFragment
import com.nickrodriguez.ciudadlimpia.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        bottomNav =
            findViewById(R.id.bottomNavigation)

        /*ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.homePrincipal)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }*/

        if(savedInstanceState == null){

            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    HomeFragment()
                )
                .commit()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {

        bottomNav.setOnItemSelectedListener {

            val fragment = when(it.itemId){

                R.id.nav_feed ->
                    HomeFragment()

                /*R.id.nav_report ->
                    ReporteFragment()*/

                /*R.id.nav_rewards ->
                    RecompensasFragment()*/

                R.id.nav_profile ->
                    ProfileFragment()

                else ->
                    HomeFragment()
            }

            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    fragment
                )
                .commit()

            true
        }
    }

    private fun logout() {

        val prefs =
            getSharedPreferences(
                "ciudad_limpia",
                MODE_PRIVATE
            )

        prefs.edit().clear().apply()

        startActivity(
            Intent(
                this,
                LoginActivity::class.java
            )
        )

        finish()
    }
}
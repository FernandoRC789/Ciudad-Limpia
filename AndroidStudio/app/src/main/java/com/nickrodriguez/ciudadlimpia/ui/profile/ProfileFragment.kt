package com.nickrodriguez.ciudadlimpia.ui.profile

// ProfileFragment.kt
// Coloca este archivo en: app/src/main/java/com/tuapp/civicguard/ui/profile/
// en mi proyecto es: app/kotlin+java/com/nickrodriguez/ciudadlimpia/ui/profile/

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.nickrodriguez.ciudadlimpia.R

/**
 * ProfileFragment
 *
 * Muestra dos estados posibles:
 *  • Estado vacío  → fragment_profile_empty.xml  (usuario nuevo, sin reportes)
 *  • Estado activo → fragment_profile.xml         (usuario con actividad)
 *
 * La decisión se toma comparando los reportes enviados del usuario actual.
 * En producción, este dato vendrá de tu ViewModel / repositorio.
 */
class ProfileFragment : Fragment() {

    // ─── Datos del usuario (reemplazar con tu ViewModel / LiveData) ────────────
    private val reportesEnviados: Int = 0       // ← 0 = usuario nuevo → estado vacío
    private val reportesAtendidos: Int = 0
    private val puntosTotales: Int = 0
    private val nombreUsuario: String = "Juan"
    // ───────────────────────────────────────────────────────────────────────────

    /** Determina si el usuario es nuevo (sin actividad registrada) */
    private val esUsuarioNuevo: Boolean
        get() = reportesEnviados == 0 && puntosTotales == 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Elige el layout según el estado del usuario
        val layoutId = if (esUsuarioNuevo) {
            R.layout.fragment_profile_empty
        } else {
            R.layout.fragment_profile
        }
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (esUsuarioNuevo) {
            setupEmptyState(view)
        } else {
            setupActiveState(view)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // ESTADO VACÍO
    // ──────────────────────────────────────────────────────────────────────────

    private fun setupEmptyState(view: View) {
        // Personaliza el saludo con el nombre real del usuario
        view.findViewById<TextView>(R.id.tvNameEmpty)?.text =
            "Bienvenido, $nombreUsuario"

        // Botón principal: navegar a la pantalla de reporte
        view.findViewById<MaterialButton>(R.id.btnHacerPrimerReporte)
            ?.setOnClickListener {
                navigateToReport()
            }

        // Link secundario: guía rápida
        view.findViewById<TextView>(R.id.tvVerComoFunciona)
            ?.setOnClickListener {
                showQuickGuide()
            }

        // Botón editar foto de perfil
        view.findViewById<View>(R.id.btnEditPhoto)
            ?.setOnClickListener {
                openImagePicker()
            }

        // Configuración
        view.findViewById<LinearLayout>(R.id.btnConfiguracion)
            ?.setOnClickListener {
                navigateToSettings()
            }

        // Cerrar sesión
        view.findViewById<LinearLayout>(R.id.btnCerrarSesion)
            ?.setOnClickListener {
                confirmLogout()
            }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // ESTADO ACTIVO (usuario con reportes y puntos)
    // ──────────────────────────────────────────────────────────────────────────

    private fun setupActiveState(view: View) {
        // Aquí conectas tus Views del fragment_profile.xml normal
        // Ejemplo:
        // view.findViewById<TextView>(R.id.tvReportesEnviados)?.text =
        //     reportesEnviados.toString()

        view.findViewById<LinearLayout>(R.id.btnConfiguracion)
            ?.setOnClickListener { navigateToSettings() }

        view.findViewById<LinearLayout>(R.id.btnCerrarSesion)
            ?.setOnClickListener { confirmLogout() }

        view.findViewById<View>(R.id.btnCanjearRecompensas)
            ?.setOnClickListener { navigateToRewards() }

        view.findViewById<View>(R.id.tvVerTodos)
            ?.setOnClickListener { navigateToAllBadges() }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // NAVEGACIÓN (reemplazar con NavController en producción)
    // ──────────────────────────────────────────────────────────────────────────

    private fun navigateToReport() {
        // Ejemplo con NavController:
        // findNavController().navigate(R.id.action_profile_to_report)
        Toast.makeText(requireContext(), "Abriendo pantalla de reporte…", Toast.LENGTH_SHORT).show()
    }

    private fun showQuickGuide() {
        // Abrir un BottomSheet o DialogFragment con la guía rápida
        Toast.makeText(requireContext(), "Mostrando guía rápida", Toast.LENGTH_SHORT).show()
    }

    private fun openImagePicker() {
        // Lanzar Intent para galería / cámara
        Toast.makeText(requireContext(), "Seleccionar foto de perfil", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToSettings() {
        // findNavController().navigate(R.id.action_profile_to_settings)
        Toast.makeText(requireContext(), "Configuración", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToRewards() {
        // findNavController().navigate(R.id.action_profile_to_rewards)
        Toast.makeText(requireContext(), "Canjear recompensas", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToAllBadges() {
        // findNavController().navigate(R.id.action_profile_to_badges)
        Toast.makeText(requireContext(), "Ver todos los badges", Toast.LENGTH_SHORT).show()
    }

    private fun confirmLogout() {
        // Mostrar AlertDialog de confirmación
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro que deseas cerrar tu sesión?")
            .setPositiveButton("Cerrar sesión") { _, _ ->
                // authViewModel.logout()
                Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}

package com.nickrodriguez.ciudadlimpia.ui.profile

// ProfileFragment.kt
// Coloca este archivo en: app/src/main/java/com/tuapp/civicguard/ui/profile/
// en mi proyecto es: app/kotlin+java/com/nickrodriguez/ciudadlimpia/ui/profile/

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.model.PerfilResponse
import com.nickrodriguez.ciudadlimpia.network.RetrofitClient
import com.nickrodriguez.ciudadlimpia.ui.auth.LoginActivity
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

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
    private lateinit var imgAvatar: ImageView

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            uri?.let {

                imgAvatar.setImageURI(it)

                val ruta = guardarImagenGaleria(it)

                actualizarFotoBackend(ruta)
            }
        }

    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->

            bitmap?.let {

                imgAvatar.setImageBitmap(it)

                val ruta =
                    guardarBitmap(bitmap)

                actualizarFotoBackend(ruta)
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_profile,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onViewCreated(view, savedInstanceState)
        configurarEventos(view)

        imgAvatar = view.findViewById(R.id.imgAvatar)
        cargarPerfil(view)
        view.findViewById<ImageButton>(R.id.btnEditarFoto)
            .setOnClickListener {

                mostrarOpcionesFoto()
            }
    }

    private fun cargarPerfil(view: View) {

        lifecycleScope.launch {

            try {

                val prefs = requireActivity()
                    .getSharedPreferences(
                        "ciudad_limpia",
                        android.content.Context.MODE_PRIVATE
                    )

                val usuarioId =
                    prefs.getLong(
                        "usuario_id",
                        0
                    )

                val token =
                    prefs.getString(
                        "jwt_token",
                        ""
                    ) ?: ""

                val response =
                    RetrofitClient
                        .apiService
                        .getPerfil(
                            "Bearer $token",
                            usuarioId
                        )

                if(response.isSuccessful){
                    val perfil =
                        response.body() ?: return@launch

                    android.util.Log.e(
                        "FOTO_TEST",
                        "fotoPerfil=${perfil.fotoPerfil}"
                    )

                    mostrarPerfil(
                        view,
                        perfil
                    )

                    if (!perfil.fotoPerfil.isNullOrEmpty()) {

                        val foto =
                            perfil.fotoPerfil
                                .replace("\"", "")

                        android.util.Log.e(
                            "FOTO_TEST",
                            "ruta_limpia=$foto"
                        )

                        val file = File(foto)

                        android.util.Log.e(
                            "FOTO_TEST",
                            "exists=${file.exists()}"
                        )

                        if (file.exists()) {

                            imgAvatar.setImageURI(
                                Uri.fromFile(file)
                            )

                        } else {

                            imgAvatar.setImageURI(
                                Uri.parse(foto)
                            )
                        }
                    }
                }

            } catch (e: Exception){

                e.printStackTrace()
            }
        }
    }

    private fun configurarEventos(view: View) {

        view.findViewById<LinearLayout>(
            R.id.btnCerrarSesion
        ).setOnClickListener {

            confirmLogout()
        }

        view.findViewById<LinearLayout>(
            R.id.btnConfiguracion
        ).setOnClickListener {

            navigateToSettings()
        }

        view.findViewById<View>(
            R.id.btnCanjearRecompensas
        ).setOnClickListener {

            navigateToRewards()
        }

        view.findViewById<View>(
            R.id.tvVerTodos
        ).setOnClickListener {

            navigateToAllBadges()
        }
    }

    private fun mostrarPerfil(
        view: View,
        perfil: PerfilResponse
    ) {

        view.findViewById<TextView>(
            R.id.tvNombreUsuario
        )?.text =
            "${perfil.nombre} ${perfil.apellido}"

        view.findViewById<TextView>(
            R.id.tvNivelUsuario
        )?.text =
            perfil.nivel.nombre

        view.findViewById<TextView>(
            R.id.tvLevelBadge
        )?.text =
            "LVL ${perfil.nivel.id}"

        view.findViewById<TextView>(
            R.id.tvReportesEnviados
        )?.text =
            perfil.totalReportes.toString()

        view.findViewById<TextView>(
            R.id.tvReportesEvaluados
        )?.text =
            perfil.reportesAtendidos.toString()

        view.findViewById<TextView>(
            R.id.tvPuntosTotales
        )?.text =
            perfil.puntosTotal.toString()
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

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private fun confirmLogout() {

        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Deseas cerrar sesión?")
            .setPositiveButton("Cerrar sesión") { _, _ ->

                val prefs =
                    requireActivity().getSharedPreferences(
                        "ciudad_limpia",
                        android.content.Context.MODE_PRIVATE
                    )

                prefs.edit().clear().apply()

                startActivity(
                    Intent(
                        requireContext(),
                        LoginActivity::class.java
                    )
                )

                requireActivity().finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarOpcionesFoto() {

        val opciones = arrayOf(
            "Tomar foto",
            "Elegir de galería"
        )

        AlertDialog.Builder(requireContext())
            .setTitle("Foto de perfil")
            .setItems(opciones) { _, which ->

                when(which){

                    0 -> cameraLauncher.launch(null)

                    1 -> galleryLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun guardarImagenGaleria(
        uri: Uri
    ): String {

        try {

            val inputStream =
                requireContext()
                    .contentResolver
                    .openInputStream(uri)
                    ?: return ""

            val file =
                File(
                    requireContext().filesDir,
                    "perfil.jpg"
                )

            file.outputStream().use { output ->

                inputStream.copyTo(output)
            }

            inputStream.close()

            val ruta = file.absolutePath

            val prefs =
                requireActivity().getSharedPreferences(
                    "ciudad_limpia",
                    android.content.Context.MODE_PRIVATE
                )

            prefs.edit()
                .putString(
                    "foto_perfil",
                    ruta
                )
                .apply()

            return ruta

        } catch (e: Exception) {

            e.printStackTrace()
            return ""
        }
    }

    private fun guardarBitmap(
        bitmap: Bitmap
    ): String {

        val file =
            File(
                requireContext().filesDir,
                "perfil.jpg"
            )

        val fos =
            FileOutputStream(file)

        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            90,
            fos
        )
        fos.close()
        val ruta = file.absolutePath
        return ruta
    }

    private fun actualizarFotoBackend(
        fotoPerfil: String
    ) {

        lifecycleScope.launch {

            try {

                val prefs =
                    requireActivity()
                        .getSharedPreferences(
                            "ciudad_limpia",
                            android.content.Context.MODE_PRIVATE
                        )

                val usuarioId =
                    prefs.getLong(
                        "usuario_id",
                        0
                    )

                val token =
                    prefs.getString(
                        "jwt_token",
                        ""
                    ) ?: ""

                val response =
                    RetrofitClient.apiService.actualizarFotoPerfil(
                        "Bearer $token",
                        usuarioId,
                        fotoPerfil
                    )

                if(response.isSuccessful){

                    Toast.makeText(
                        requireContext(),
                        "Foto actualizada",
                        Toast.LENGTH_SHORT
                    ).show()

                }else{

                    Toast.makeText(
                        requireContext(),
                        "Error al guardar foto",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}

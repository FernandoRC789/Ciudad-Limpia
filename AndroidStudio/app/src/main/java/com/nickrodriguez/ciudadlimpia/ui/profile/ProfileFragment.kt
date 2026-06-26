package com.nickrodriguez.ciudadlimpia.ui.profile

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.model.PerfilResponse
import com.nickrodriguez.ciudadlimpia.network.RetrofitClient
import com.nickrodriguez.ciudadlimpia.ui.auth.LoginActivity
import com.nickrodriguez.ciudadlimpia.viewmodel.SharedProfileViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * ProfileFragment — CORREGIDO
 *
 * CAMBIO CLAVE: usa el mismo SharedProfileViewModel que HomeFragment
 * (vía activityViewModels(), no viewModels()). Si el usuario ya visitó
 * Home, el perfil ya está en memoria y esta pantalla aparece instantánea
 * — sin nueva llamada HTTP, sin pantalla en blanco.
 */
class ProfileFragment : Fragment() {

    private val profileViewModel: SharedProfileViewModel by activityViewModels()

    private lateinit var imgAvatar: ImageView

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imgAvatar.setImageURI(it)
            val ruta = guardarImagenGaleria(it)
            actualizarFotoBackend(ruta)
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            imgAvatar.setImageBitmap(it)
            val ruta = guardarBitmap(it)
            actualizarFotoBackend(ruta)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgAvatar = view.findViewById(R.id.imgAvatar)

        configurarEventos(view)
        observeViewModel(view)
        triggerDataLoad()

        view.findViewById<ImageButton>(R.id.btnEditarFoto)
            .setOnClickListener { mostrarOpcionesFoto() }
    }

    // ── Observa los mismos LiveData que Home — datos compartidos, no duplicados ─
    private fun observeViewModel(view: View) {
        profileViewModel.perfil.observe(viewLifecycleOwner) { perfil ->
            perfil?.let { mostrarPerfil(view, it) }
        }
    }

    private fun triggerDataLoad() {
        val prefs = requireActivity().getSharedPreferences(
            "ciudad_limpia", android.content.Context.MODE_PRIVATE
        )
        val token = prefs.getString("jwt_token", "") ?: ""
        val usuarioId = prefs.getLong("usuario_id", 0)

        // Si Home ya cargó el perfil, esto no hace ninguna llamada de red
        profileViewModel.cargarPerfilSiEsNecesario(token, usuarioId)
    }
    private fun configurarEventos(view: View) {
        view.findViewById<LinearLayout>(R.id.btnCerrarSesion)
            .setOnClickListener { confirmLogout() }

        view.findViewById<LinearLayout>(R.id.btnConfiguracion)
            .setOnClickListener { navigateToSettings() }

        view.findViewById<View>(R.id.btnCanjearRecompensas)
            .setOnClickListener { navigateToRewards() }

//        view.findViewById<View>(R.id.tvVerTodos)
//            .setOnClickListener { navigateToAllBadges() }
    }

    private fun mostrarPerfil(view: View, perfil: PerfilResponse) {
        view.findViewById<TextView>(R.id.tvNombreUsuario)?.text =
            "${perfil.nombre} ${perfil.apellido}"
        view.findViewById<TextView>(R.id.tvNivelUsuario)?.text = perfil.nivel.nombre
        view.findViewById<TextView>(R.id.tvReportesEnviados)?.text =
            perfil.totalReportes.toString()
        view.findViewById<TextView>(R.id.tvReportesEvaluados)?.text =
            perfil.reportesAtendidos.toString()
        view.findViewById<TextView>(R.id.tvPuntosTotales)?.text =
            perfil.puntosTotal.toString()

        cargarAvatar(perfil.fotoPerfil)
    }

    private fun cargarAvatar(fotoPerfil: String?) {
        if (fotoPerfil.isNullOrEmpty()) return

        val foto = fotoPerfil.replace("\"", "")
        val file = File(foto)

        if (file.exists()) {
            imgAvatar.setImageURI(Uri.fromFile(file))
        } else {
            imgAvatar.setImageURI(Uri.parse(foto))
        }
    }

    // ── Navegación ────────────────────────────────────────────────────────────

    private fun navigateToSettings() {
        Toast.makeText(requireContext(), "Configuración", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToRewards() {
        Toast.makeText(requireContext(), "Canjear recompensas", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToAllBadges() {
        Toast.makeText(requireContext(), "Ver todos los badges", Toast.LENGTH_SHORT).show()
    }

    private fun confirmLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Deseas cerrar sesión?")
            .setPositiveButton("Cerrar sesión") { _, _ ->
                val prefs = requireActivity().getSharedPreferences(
                    "ciudad_limpia", android.content.Context.MODE_PRIVATE
                )
                //prefs.edit().clear().apply()
                prefs.edit()
                    .remove("jwt_token")
                    .remove("rol")
                    .remove("usuario_id")
                    .remove("foto_perfil") // opcional, si también quieres limpiar la foto local
                    .apply()

                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarOpcionesFoto() {
        val opciones = arrayOf("Tomar foto", "Elegir de galería")

        AlertDialog.Builder(requireContext())
            .setTitle("Foto de perfil")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> cameraLauncher.launch(null)
                    1 -> galleryLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun guardarImagenGaleria(uri: Uri): String {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
                ?: return ""
            val file = File(requireContext().filesDir, "perfil.jpg")
            file.outputStream().use { output -> inputStream.copyTo(output) }
            inputStream.close()

            val ruta = file.absolutePath
            requireActivity().getSharedPreferences(
                "ciudad_limpia", android.content.Context.MODE_PRIVATE
            ).edit().putString("foto_perfil", ruta).apply()

            ruta
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun guardarBitmap(bitmap: Bitmap): String {
        val file = File(requireContext().filesDir, "perfil.jpg")
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
        }
        return file.absolutePath
    }

    private fun actualizarFotoBackend(fotoPerfil: String) {
        lifecycleScope.launch {
            try {
                val prefs = requireActivity().getSharedPreferences(
                    "ciudad_limpia", android.content.Context.MODE_PRIVATE
                )
                val usuarioId = prefs.getLong("usuario_id", 0)
                val token = prefs.getString("jwt_token", "") ?: ""

                val response = RetrofitClient.apiService.actualizarFotoPerfil(
                    "Bearer $token", usuarioId, fotoPerfil
                )

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Foto actualizada", Toast.LENGTH_SHORT).show()
                    // Importante: invalida la caché para que se refresque en ambas pantallas
                    profileViewModel.invalidarCache()
                } else {
                    Toast.makeText(requireContext(), "Error al guardar foto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
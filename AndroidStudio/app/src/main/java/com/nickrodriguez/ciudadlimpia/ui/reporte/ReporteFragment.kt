package com.nickrodriguez.ciudadlimpia.ui.reporte

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.nickrodriguez.ciudadlimpia.MainActivity
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.adapter.reporte.FotoAdapter
import com.nickrodriguez.ciudadlimpia.model.PerfilResponse
import com.nickrodriguez.ciudadlimpia.model.ReporteRequest
import com.nickrodriguez.ciudadlimpia.network.CloudinaryService
import com.nickrodriguez.ciudadlimpia.network.RetrofitClient
import com.nickrodriguez.ciudadlimpia.utils.SessionManager
import com.nickrodriguez.ciudadlimpia.viewmodel.SharedProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class ReporteFragment : Fragment() {

    private val profileViewModel: SharedProfileViewModel by activityViewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val cancellationTokenSource = CancellationTokenSource()

    private var latitud = 0.0
    private var longitud = 0.0

    private lateinit var cardFoto: MaterialCardView
    private lateinit var layoutSubirFoto: LinearLayout
    private lateinit var etTitulo: TextInputEditText
    private lateinit var etDescripcion: TextInputEditText
    private lateinit var etDireccion: TextInputEditText
    private lateinit var tvDireccionDetectada: TextView
    private lateinit var btnEnviarReporte: MaterialButton
    private lateinit var rvFotos: RecyclerView

    private val imagenesSeleccionadas = mutableListOf<Uri>()
    private lateinit var fotoAdapter: FotoAdapter

    // URI donde se guardará la foto de cámara
    private var uriFotoCamara: Uri? = null

    // Cámara real — guarda en archivo y devuelve Uri
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
            if (exito) {
                uriFotoCamara?.let { uri ->
                    if (imagenesSeleccionadas.size < 4) {
                        imagenesSeleccionadas.add(uri)
                        fotoAdapter.notifyItemInserted(imagenesSeleccionadas.size - 1)
                        actualizarVistaFotos()
                    }
                }
            }
        }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) abrirCamara()
            else Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }

    private val seleccionarGaleria =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            uris.forEach {
                if (imagenesSeleccionadas.size < 4) {
                    imagenesSeleccionadas.add(it)
                    fotoAdapter.notifyItemInserted(imagenesSeleccionadas.size - 1)
                }
            }
            actualizarVistaFotos()
        }

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) obtenerUbicacion()
            else {
                tvDireccionDetectada.text = "Permiso de ubicación denegado"
                Toast.makeText(requireContext(), "Necesitamos tu ubicación para registrar el reporte", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_registrar_reporte, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardFoto = view.findViewById(R.id.cardFoto)
        layoutSubirFoto = view.findViewById(R.id.layoutSubirFoto)
        etTitulo = view.findViewById(R.id.etTitulo)
        etDescripcion = view.findViewById(R.id.etDescripcion)
        etDireccion = view.findViewById(R.id.etDireccion)
        tvDireccionDetectada = view.findViewById(R.id.tvDireccionDetectada)
        btnEnviarReporte = view.findViewById(R.id.btnEnviarReporte)
        rvFotos = view.findViewById(R.id.rvFotos)

        // Setup RecyclerView
        fotoAdapter = FotoAdapter(imagenesSeleccionadas) { position ->
            imagenesSeleccionadas.removeAt(position)
            fotoAdapter.notifyItemRemoved(position)
            fotoAdapter.notifyItemRangeChanged(position, imagenesSeleccionadas.size)
            actualizarVistaFotos()
        }
        rvFotos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvFotos.adapter = fotoAdapter

        cardFoto.setOnClickListener { mostrarOpcionesFoto() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        verificarPermisoUbicacion()

        btnEnviarReporte.setOnClickListener { enviarReporte() }

        observeViewModel(view)
    }

    private fun actualizarVistaFotos() {
        if (imagenesSeleccionadas.isEmpty()) {
            layoutSubirFoto.visibility = View.VISIBLE
            rvFotos.visibility = View.GONE
        } else {
            layoutSubirFoto.visibility = View.GONE
            rvFotos.visibility = View.VISIBLE
        }
    }

    private fun abrirCamara() {
        // Crear archivo temporal para guardar la foto
        val archivoFoto = File.createTempFile("foto_camara_", ".jpg", requireContext().cacheDir)
        uriFotoCamara = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            archivoFoto
        )
        takePicture.launch(uriFotoCamara)
    }

    private fun mostrarOpcionesFoto() {
        if (imagenesSeleccionadas.size >= 4) {
            Toast.makeText(requireContext(), "Máximo 4 fotos", Toast.LENGTH_SHORT).show()
            return
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Agregar evidencia")
            .setItems(arrayOf("Tomar foto", "Seleccionar de galería")) { _, which ->
                when (which) {
                    0 -> {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                            abrirCamara()
                        } else {
                            requestCameraPermission.launch(Manifest.permission.CAMERA)
                        }
                    }
                    1 -> seleccionarGaleria.launch("image/*")
                }
            }.show()
    }

    // ---------- UBICACIÓN ----------

    private fun verificarPermisoUbicacion() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion()
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
        tvDireccionDetectada.text = "Obteniendo ubicación..."
        val locationRequest = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()

        fusedLocationClient.getCurrentLocation(locationRequest, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    latitud = location.latitude
                    longitud = location.longitude
                    obtenerDireccionDesdeCoordenadas(latitud, longitud)
                } else {
                    tvDireccionDetectada.text = "No se pudo obtener ubicación, activa el GPS"
                }
            }
            .addOnFailureListener {
                tvDireccionDetectada.text = "Error al obtener ubicación"
            }
    }

    private fun obtenerDireccionDesdeCoordenadas(lat: Double, lon: Double) {
        val geocoder = Geocoder(requireContext(), Locale("es", "PE"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lon, 1) { direcciones ->
                activity?.runOnUiThread { manejarDireccionObtenida(direcciones) }
            }
        } else {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val direcciones = try {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(lat, lon, 1)
                } catch (e: Exception) { null }
                activity?.runOnUiThread { manejarDireccionObtenida(direcciones) }
            }
        }
    }

    private fun manejarDireccionObtenida(direcciones: List<Address>?) {
        if (!direcciones.isNullOrEmpty()) {
            val direccionTexto = direcciones[0].getAddressLine(0) ?: "Dirección no disponible"
            tvDireccionDetectada.text = direccionTexto
            if (etDireccion.text.isNullOrEmpty()) etDireccion.setText(direccionTexto)
        } else {
            tvDireccionDetectada.text = "No se pudo determinar la dirección"
        }
    }

    // ---------- ENVÍO ----------

    private fun enviarReporte() {
        val titulo = etTitulo.text?.toString()?.trim().orEmpty()
        val descripcion = etDescripcion.text?.toString()?.trim().orEmpty()
        val direccion = etDireccion.text?.toString()?.trim().orEmpty()

        if (imagenesSeleccionadas.isEmpty()) {
            Toast.makeText(requireContext(), "Agrega al menos una foto como evidencia", Toast.LENGTH_LONG).show()
            return
        }

        if (titulo.isEmpty()) { etTitulo.error = "Ingresa un título"; return }
        if (descripcion.isEmpty()) { etDescripcion.error = "Describe el problema"; return }
        if (direccion.isEmpty()) { etDireccion.error = "Ingresa una dirección"; return }
        if (latitud == 0.0 && longitud == 0.0) {
            Toast.makeText(requireContext(), "Espera a que se detecte tu ubicación", Toast.LENGTH_LONG).show()
            return
        }

        btnEnviarReporte.isEnabled = false
        btnEnviarReporte.text = "Enviando..."

        val token = SessionManager(requireContext()).getToken()
        if (token == null) {
            Toast.makeText(requireContext(), "Sesión expirada", Toast.LENGTH_LONG).show()
            btnEnviarReporte.isEnabled = true
            btnEnviarReporte.text = "Enviar Reporte"
            return
        }

        if (imagenesSeleccionadas.isNotEmpty()) {
            val urlsSubidas = mutableListOf<String>()
            var procesadas = 0

            imagenesSeleccionadas.forEach { uri ->
                val archivo = uriToFile(uri)
                if (archivo == null) {
                    procesadas++
                    if (procesadas == imagenesSeleccionadas.size)
                        activity?.runOnUiThread { enviarReporteAlBackend(titulo, descripcion, direccion, token, urlsSubidas) }
                    return@forEach
                }

                CloudinaryService.subirFoto(
                    archivo = archivo,
                    onExito = { url ->
                        urlsSubidas.add(url)
                        procesadas++
                        if (procesadas == imagenesSeleccionadas.size)
                            activity?.runOnUiThread { enviarReporteAlBackend(titulo, descripcion, direccion, token, urlsSubidas) }
                    },
                    onError = {
                        procesadas++
                        if (procesadas == imagenesSeleccionadas.size)
                            activity?.runOnUiThread { enviarReporteAlBackend(titulo, descripcion, direccion, token, urlsSubidas) }
                    }
                )
            }
        } else {
            enviarReporteAlBackend(titulo, descripcion, direccion, token, emptyList())
        }
    }

    private fun enviarReporteAlBackend(titulo: String, descripcion: String, direccion: String, token: String, fotos: List<String>) {
        val reporte = ReporteRequest(titulo, descripcion, latitud, longitud, direccion, fotos)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.crearReporte("Bearer $token", reporte)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "✅ Reporte enviado correctamente", Toast.LENGTH_LONG).show()
                    limpiarFormulario()
                    irAHome()
                } else {
                    Toast.makeText(requireContext(), "Error del servidor (${response.code()})", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                btnEnviarReporte.isEnabled = true
                btnEnviarReporte.text = "Enviar Reporte"
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("foto_", ".jpg", requireContext().cacheDir)
            tempFile.outputStream().use { output -> inputStream.copyTo(output) }
            tempFile
        } catch (e: Exception) { null }
    }

    private fun irAHome() {
        val activity = requireActivity() as MainActivity
        activity.findViewById<BottomNavigationView>(R.id.bottomNavigation).selectedItemId = R.id.nav_feed
    }

    private fun limpiarFormulario() {
        etTitulo.text?.clear()
        etDescripcion.text?.clear()
        imagenesSeleccionadas.clear()
        fotoAdapter.notifyDataSetChanged()
        actualizarVistaFotos()
    }

    private fun observeViewModel(view: View) {
        profileViewModel.perfil.observe(viewLifecycleOwner) { perfil ->
            perfil?.let { actualizarHeader(it, view) }
        }
    }

    private fun actualizarHeader(perfil: PerfilResponse, view: View) {
        view.findViewById<TextView>(R.id.tvAppName)?.text = "${perfil.nombre} ${perfil.apellido}"
        view.findViewById<TextView>(R.id.tvUserRank)?.text = perfil.nivel.nombre
        view.findViewById<TextView>(R.id.tvPoints)?.text = "${perfil.puntosTotal} pts"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancellationTokenSource.cancel()
    }
}
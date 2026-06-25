package com.nickrodriguez.ciudadlimpia.ui.reporte

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.nickrodriguez.ciudadlimpia.R
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import androidx.activity.result.contract.ActivityResultContracts
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.nickrodriguez.ciudadlimpia.model.ReporteRequest
import com.nickrodriguez.ciudadlimpia.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class ReporteFragment : Fragment() {

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

    private val imagenesSeleccionadas = mutableListOf<Uri>()

    private val takePicture =
        registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            bitmap?.let {
                Toast.makeText(requireContext(), "Foto capturada", Toast.LENGTH_SHORT).show()
                layoutSubirFoto.visibility = View.GONE
            }
        }

    private val requestCameraPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                takePicture.launch(null)
            } else {
                Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

    private val seleccionarGaleria =
        registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uris ->
            uris.forEach {
                if (imagenesSeleccionadas.size < 4) {
                    imagenesSeleccionadas.add(it)
                }
            }
            actualizarGaleria()
        }

    private val requestLocationPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                obtenerUbicacion()
            } else {
                tvDireccionDetectada.text = "Permiso de ubicación denegado"
                Toast.makeText(
                    requireContext(),
                    "Necesitamos tu ubicación para registrar el reporte",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        cardFoto.setOnClickListener {
            mostrarOpcionesFoto()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        verificarPermisoUbicacion()

        btnEnviarReporte.setOnClickListener {
            enviarReporte()
        }
    }

    // ---------- UBICACIÓN ----------

    private fun verificarPermisoUbicacion() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            obtenerUbicacion()
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
        tvDireccionDetectada.text = "Obteniendo ubicación..."

        val locationRequest = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

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
                Toast.makeText(requireContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerDireccionDesdeCoordenadas(lat: Double, lon: Double) {
        val geocoder = Geocoder(requireContext(), Locale("es", "PE"))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lon, 1) { direcciones ->
                activity?.runOnUiThread {
                    manejarDireccionObtenida(direcciones)
                }
            }
        } else {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val direcciones = try {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(lat, lon, 1)
                } catch (e: Exception) {
                    null
                }
                activity?.runOnUiThread {
                    manejarDireccionObtenida(direcciones)
                }
            }
        }
    }

    private fun manejarDireccionObtenida(direcciones: List<Address>?) {
        if (!direcciones.isNullOrEmpty()) {
            val direccionTexto = direcciones[0].getAddressLine(0) ?: "Dirección no disponible"
            tvDireccionDetectada.text = direccionTexto
            if (etDireccion.text.isNullOrEmpty()) {
                etDireccion.setText(direccionTexto)
            }
        } else {
            tvDireccionDetectada.text = "No se pudo determinar la dirección"
        }
    }

    // ---------- FOTOS (sin cambios) ----------

    private fun mostrarOpcionesFoto() {
        val opciones = arrayOf("Tomar foto", "Seleccionar de galería")

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar evidencia")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> {
                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            takePicture.launch(null)
                        } else {
                            requestCameraPermission.launch(Manifest.permission.CAMERA)
                        }
                    }
                    1 -> seleccionarGaleria.launch("image/*")
                }
            }
            .show()
    }

    private fun actualizarGaleria() {
        Toast.makeText(
            requireContext(),
            "Fotos seleccionadas: ${imagenesSeleccionadas.size}",
            Toast.LENGTH_SHORT
        ).show()
    }

    // ---------- ENVÍO DEL REPORTE ----------

    private fun enviarReporte() {
        val titulo = etTitulo.text?.toString()?.trim().orEmpty()
        val descripcion = etDescripcion.text?.toString()?.trim().orEmpty()
        val direccion = etDireccion.text?.toString()?.trim().orEmpty()

        if (titulo.isEmpty()) {
            etTitulo.error = "Ingresa un título"
            return
        }
        if (descripcion.isEmpty()) {
            etDescripcion.error = "Describe el problema"
            return
        }
        if (direccion.isEmpty()) {
            etDireccion.error = "Ingresa una dirección"
            return
        }
        if (latitud == 0.0 && longitud == 0.0) {
            Toast.makeText(
                requireContext(),
                "Aún no tenemos tu ubicación, espera unos segundos e intenta de nuevo",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val reporte = ReporteRequest(
            titulo = titulo,
            descripcion = descripcion,
            latitud = latitud,
            longitud = longitud,
            direccion = direccion
        )

        btnEnviarReporte.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.crearReporte(reporte)

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Reporte enviado correctamente", Toast.LENGTH_LONG).show()
                    limpiarFormulario()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error del servidor (${response.code()})",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Sin conexión o error de red: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                btnEnviarReporte.isEnabled = true
            }
        }
    }

    private fun limpiarFormulario() {
        etTitulo.text?.clear()
        etDescripcion.text?.clear()
        // la dirección y ubicación se mantienen por si quiere enviar otro reporte cercano
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancellationTokenSource.cancel()
    }
}
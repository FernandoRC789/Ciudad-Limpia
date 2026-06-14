package com.nickrodriguez.ciudadlimpia.ui.reporte

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nickrodriguez.ciudadlimpia.R

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.card.MaterialCardView
import androidx.activity.result.contract.ActivityResultContracts
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class ReporteFragment : Fragment() {

    private lateinit var fusedLocationClient:
            FusedLocationProviderClient

    private var latitud = 0.0
    private var longitud = 0.0

    private lateinit var cardFoto: MaterialCardView
    private lateinit var layoutSubirFoto: LinearLayout

    private val imagenesSeleccionadas =
        mutableListOf<Uri>()
    private val takePicture =
        registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->

            bitmap?.let {

                Toast.makeText(
                    requireContext(),
                    "Foto capturada",
                    Toast.LENGTH_SHORT
                ).show()

                layoutSubirFoto.visibility = View.GONE
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_registrar_reporte,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        cardFoto = view.findViewById(R.id.cardFoto)

        layoutSubirFoto =
            view.findViewById(R.id.layoutSubirFoto)

        cardFoto.setOnClickListener {
            mostrarOpcionesFoto()
        }

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(
                requireActivity()
            )

        obtenerUbicacion()
    }


    private fun obtenerUbicacion() {

        Toast.makeText(
            requireContext(),
            "Obteniendo ubicación...",
            Toast.LENGTH_SHORT
        ).show()
    }

    private val requestCameraPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                takePicture.launch(null)

            } else {

                Toast.makeText(
                    requireContext(),
                    "Permiso de cámara denegado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun mostrarOpcionesFoto() {

        val opciones = arrayOf(
            "Tomar foto",
            "Seleccionar de galería"
        )

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar evidencia")
            .setItems(opciones) { _, which ->

                when(which){

                    0 -> {

                        if (
                            ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {

                            takePicture.launch(null)

                        } else {

                            requestCameraPermission.launch(
                                Manifest.permission.CAMERA
                            )
                        }
                    }
                    1 -> seleccionarGaleria.launch(
                        "image/*"
                    )
                }
            }
            .show()
    }

    private val seleccionarGaleria =
        registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uris ->

            uris.forEach {

                if(imagenesSeleccionadas.size < 4){

                    imagenesSeleccionadas.add(it)
                }
            }

            actualizarGaleria()
        }

    private fun actualizarGaleria() {

        Toast.makeText(
            requireContext(),
            "Fotos seleccionadas: ${imagenesSeleccionadas.size}",
            Toast.LENGTH_SHORT
        ).show()
    }
}
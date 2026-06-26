package com.nickrodriguez.ciudadlimpia.model

data class ReporteRequest(
    val titulo: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double,
    val direccion: String,
    val fotos: List<String> = emptyList() // URLs de Cloudinary
)

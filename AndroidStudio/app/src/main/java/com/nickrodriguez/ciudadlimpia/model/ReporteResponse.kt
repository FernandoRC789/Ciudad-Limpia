package com.nickrodriguez.ciudadlimpia.model

data class ReporteResponse(

    val id: Long,

    val titulo: String,

    val descripcion: String,

    val latitud: String,

    val longitud: String,

    val direccion: String,

    val estado: String,

    val puntosOtorgados: Int,

    val createdAt: String,

    val updatedAt: String,

    val usuario: Long
)
package com.nickrodriguez.ciudadlimpia.model

data class PerfilResponse(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val email: String,
    val fotoPerfil: String?,
    val puntosTotal: Int,
    val nivel: NivelInfo,
    val siguienteNivel: String,
    val totalReportes: Int,
    val reportesAtendidos: Int
)
data class NivelInfo(
    val id: Int,
    val nombre: String,
    val icono: String,
    val puntosParaSiguienteNivel: Int
)
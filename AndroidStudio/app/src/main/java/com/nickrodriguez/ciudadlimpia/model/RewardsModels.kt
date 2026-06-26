package com.nickrodriguez.ciudadlimpia.model

import com.google.gson.annotations.SerializedName
/* ======================================================================
 *  MODELOS DE REWARDS — Ajusta los @SerializedName si tu backend responde
 *  con nombres distintos a los que se asumen aquí. Verifica con Logcat
 *  (el logging de OkHttp ya está activo en RetrofitClient) y corrige solo
 *  los nombres; el resto del código no necesita cambiar.
 * ====================================================================== */

// ---------- GET /api/perfil/puntos/historial ----------
data class HistorialPuntosResponse(
    @SerializedName("puntosActuales") val puntosActuales: Int = 0,
    @SerializedName("nivelActual") val nivelActual: Int = 0,
    @SerializedName("puntosParaSiguienteNivel") val puntosParaSiguienteNivel: Int = 0,
    @SerializedName("porcentajeProgreso") val porcentajeProgreso: Int = 0,
    @SerializedName("insignias") val insignias: List<Insignia>? = null,
    @SerializedName("historial") val historial: List<MovimientoPuntos>? = null
)

data class Insignia(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("icono") val icono: String? = null,
    @SerializedName("obtenida") val obtenida: Boolean = true
)

data class MovimientoPuntos(
    val id: Long,
    val puntos: Int,
    val tipo: String,
    val motivo: String,
    val createdAt: String,
    val gamingProfile: Long,
    val reporte: Long
)

// ---------- GET /api/ranking ----------
data class RankingItem(
    val posicion: Int,
    val nombreCompleto: String,
    val nivel: String,
    val puntosTotal: Int,
    val totalReportes: Int,
    val usuarioId: Long = 0
)

// Si tu /api/ranking devuelve un objeto envoltorio en vez de un array directo
// ({ "ranking": [...], "posicionUsuarioActual": N }), cambia el tipo de
// retorno de ApiService.getRanking() a Response<RankingResponse> y usa este
// modelo en su lugar.
data class RankingResponse(
    @SerializedName("ranking") val ranking: List<RankingItem> = emptyList(),
    @SerializedName("posicionUsuarioActual") val posicionUsuarioActual: Int? = null,
    @SerializedName("distrito") val distrito: String? = null
)

// ---------- GET /api/cupons/disponibles ----------
data class Cupon(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("descripcion") val descripcion: String = "",
    @SerializedName("costoPuntos") val costoPuntos: Int = 0,
    @SerializedName("stock") val stock: Int = 0,
    @SerializedName("fechaExpiracion") val fechaExpiracion: String? = null,
    @SerializedName("activo") val activo: Boolean = true,
    @SerializedName("imagenUrl") val imagenUrl: String? = null
)

// ---------- POST /api/canjes/cupon/{id} ----------
data class CanjeResponse(
    @SerializedName("mensaje") val mensaje: String? = null,
    @SerializedName("puntosRestantes") val puntosRestantes: Int? = null,
    @SerializedName("exito") val exito: Boolean = true
)

/* ======================================================================
 *  MODELOS — Ajusta los nombres de los campos (@SerializedName) si tu
 *  backend responde con nombres distintos a los que se asumen aquí.
 *  Revisa la respuesta real con Logcat (ver ApiClient con logging) y
 *  corrige los @SerializedName si hace falta — el resto del código no
 *  necesita cambiar.
 * ====================================================================== */

// ---------- GET /api/perfil/puntos/historial ----------
/*data class HistorialPuntosResponse(
    @SerializedName("puntosActuales") val puntosActuales: Int = 0,
    @SerializedName("nivelActual") val nivelActual: Int = 0,
    @SerializedName("puntosParaSiguienteNivel") val puntosParaSiguienteNivel: Int = 0,
    @SerializedName("porcentajeProgreso") val porcentajeProgreso: Int = 0,
    @SerializedName("insignias") val insignias: List<Insignia>? = null,
    @SerializedName("historial") val historial: List<MovimientoPuntos>? = null
)

data class Insignia(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("icono") val icono: String? = null, // url o nombre de ícono
    @SerializedName("obtenida") val obtenida: Boolean = true
)

data class MovimientoPuntos(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("concepto") val concepto: String = "",
    @SerializedName("puntos") val puntos: Int = 0,
    @SerializedName("fecha") val fecha: String? = null
)

// ---------- GET /api/ranking ----------
data class RankingItem(
    @SerializedName("posicion") val posicion: Int = 0,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("titulo") val titulo: String? = null, // "ESTRATEGA URBANO", etc.
    @SerializedName("puntos") val puntos: Int = 0,
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("esUsuarioActual") val esUsuarioActual: Boolean = false
)

// Algunos backends envuelven la lista + la posición del usuario actual en un objeto.
// Si tu /api/ranking devuelve directamente un array, usa Endpoints.getRankingLista()
// (ver ApiService) en lugar de este wrapper.
data class RankingResponse(
    @SerializedName("ranking") val ranking: List<RankingItem> = emptyList(),
    @SerializedName("posicionUsuarioActual") val posicionUsuarioActual: Int? = null,
    @SerializedName("distrito") val distrito: String? = null
)

// ---------- GET /api/cupons/disponibles ----------
data class Cupon(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("descripcion") val descripcion: String = "",
    @SerializedName("costoPuntos") val costoPuntos: Int = 0,
    @SerializedName("stock") val stock: Int = 0,
    @SerializedName("fechaExpiracion") val fechaExpiracion: String? = null,
    @SerializedName("activo") val activo: Boolean = true,
    @SerializedName("imagenUrl") val imagenUrl: String? = null
)

// ---------- POST /api/canjes/cupon/{id} ----------
// Respuesta esperada al canjear. Si tu backend devuelve algo distinto
// (p.ej. solo un 200 OK sin body), cambia CanjeResponse por Unit o
// ResponseBody en ApiService.
data class CanjeResponse(
    @SerializedName("mensaje") val mensaje: String? = null,
    @SerializedName("puntosRestantes") val puntosRestantes: Int? = null,
    @SerializedName("exito") val exito: Boolean = true
)

// Modelo de error genérico (muchos backends Spring Boot devuelven esta forma
// cuando algo falla, p.ej. puntos insuficientes -> 400 Bad Request)
data class ErrorResponse(
    @SerializedName("error") val error: String? = null,
    @SerializedName("mensaje") val mensaje: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("status") val status: Int? = null
)*/

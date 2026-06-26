package com.nickrodriguez.ciudadlimpia.network

import com.nickrodriguez.ciudadlimpia.model.AuthRequest
import com.nickrodriguez.ciudadlimpia.model.AuthResponse
import com.nickrodriguez.ciudadlimpia.model.CanjeResponse
import com.nickrodriguez.ciudadlimpia.model.Cupon
import com.nickrodriguez.ciudadlimpia.model.HistorialPuntosResponse
import com.nickrodriguez.ciudadlimpia.model.MovimientoPuntos
import com.nickrodriguez.ciudadlimpia.model.PerfilResponse
import com.nickrodriguez.ciudadlimpia.model.RankingItem
import com.nickrodriguez.ciudadlimpia.model.RegisterUserRequest
import com.nickrodriguez.ciudadlimpia.model.ReporteRequest
import com.nickrodriguez.ciudadlimpia.model.ReporteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: AuthRequest
    ): Response<AuthResponse>

    @POST("api/auth/registro")
    suspend fun register(
        @Body request: RegisterUserRequest
    ): Response<AuthResponse>

    @GET("api/perfil")
    suspend fun getPerfil(
        @Header("Authorization") token: String,
        //@Path("id") id: Long
    ): Response<PerfilResponse>

    @PUT("api/perfil/{id}/foto")
    suspend fun actualizarFotoPerfil(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body fotoPerfil: String
    ): Response<Void>

    @GET("api/reportes/mis-reportes")
    suspend fun getMisReportes(
        @Header("Authorization") token: String
    ): Response<List<ReporteResponse>>

    @POST("api/reportes")
    suspend fun crearReporte(
        @Header("Authorization") token: String,
        @Body reporte: ReporteRequest
    ): Response<ReporteResponse>



    /** Puntos, nivel, insignias e historial del usuario logueado. */
    @GET("api/perfil/puntos/historial")
    suspend fun getHistorialPuntos(
        @Header("Authorization") token: String
    ): Response<List<MovimientoPuntos>>

    /**
     * Ranking público. Se asume que el backend devuelve un ARRAY directo
     * (List<RankingItem>). Si en cambio devuelve un objeto envoltorio
     * { "ranking": [...], "posicionUsuarioActual": N }, usa RankingResponse
     * (ya definido en RewardsModels.kt) como tipo de retorno en su lugar.
     */
    @GET("api/ranking")
    suspend fun getRanking(
        @Header("Authorization") token: String
    ): Response<List<RankingItem>>

    /** Catálogo de cupones disponibles para canjear. */
    @GET("api/cupons/disponibles")
    suspend fun getCuponesDisponibles(
        @Header("Authorization") token: String
    ): Response<List<Cupon>>

    /** Canjea un cupón por su id, descontando los puntos del usuario. */
    @POST("api/canjes/cupon/{id}")
    suspend fun canjearCupon(
        @Header("Authorization") token: String,
        @Path("id") cuponId: Int
    ): Response<CanjeResponse>
}
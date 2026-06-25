package com.nickrodriguez.ciudadlimpia.network

import com.nickrodriguez.ciudadlimpia.model.AuthRequest
import com.nickrodriguez.ciudadlimpia.model.AuthResponse
import com.nickrodriguez.ciudadlimpia.model.PerfilResponse
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

    @POST("reportes") // 👈 AJUSTA esto al endpoint real de tu backend
    suspend fun crearReporte(@Body reporte: ReporteRequest): Response<ReporteResponse>
}
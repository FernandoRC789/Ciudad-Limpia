package com.nickrodriguez.ciudadlimpia.network

import com.nickrodriguez.ciudadlimpia.model.AuthRequest
import com.nickrodriguez.ciudadlimpia.model.AuthResponse
import com.nickrodriguez.ciudadlimpia.model.RegisterUserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: AuthRequest
    ): Response<AuthResponse>

    @POST("api/auth/registro")
    suspend fun register(
        @Body request: RegisterUserRequest
    ): Response<AuthResponse>
}
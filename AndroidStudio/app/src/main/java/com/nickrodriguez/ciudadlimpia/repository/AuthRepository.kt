package com.nickrodriguez.ciudadlimpia.repository

import com.nickrodriguez.ciudadlimpia.model.AuthRequest
import com.nickrodriguez.ciudadlimpia.model.AuthResponse
import com.nickrodriguez.ciudadlimpia.network.RetrofitClient
import retrofit2.Response

class AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Response<AuthResponse> {

        return RetrofitClient
            .apiService
            .login(
                AuthRequest(
                    email,
                    password
                )
            )
    }
}
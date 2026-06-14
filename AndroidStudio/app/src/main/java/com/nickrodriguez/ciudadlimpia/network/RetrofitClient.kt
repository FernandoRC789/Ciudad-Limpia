package com.nickrodriguez.ciudadlimpia.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    //Si usarás el emulador Android:

    /*private const val BASE_URL =
        "http://10.0.2.2:8080/"*/

    //Si usarás tu celular físico:

    private const val BASE_URL =
        //"http://10.228.208.191:8080/"
    "http://192.168.1.6:8080/"


    private val retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
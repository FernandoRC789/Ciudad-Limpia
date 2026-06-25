package com.nickrodriguez.ciudadlimpia.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient — CORREGIDO
 *
 * CAMBIOS:
 * 1. Timeouts explícitos (antes no había ninguno — si tu backend tardaba,
 *    la app podía colgarse esperando indefinidamente).
 * 2. HttpLoggingInterceptor para ver en Logcat cuánto tarda cada request
 *    y diagnosticar si el lag es del backend o de la app.
 * 3. OkHttpClient compartido y reusado (antes Retrofit no especificaba
 *    uno, usaba el cliente default sin ningún ajuste).
 *
 * IMPORTANTE: revisa el BASE_URL — debe coincidir con la red de tu celular.
 */
object RetrofitClient {
    //Si usarás el emulador Android:

    /*private const val BASE_URL =
        "http://10.0.2.2:8080/"*/
    //Si usarás tu celular físico:

    private const val BASE_URL =
        //"http://10.228.208.191:8080/"
    //"http://192.168.1.5:8080/"
        "https://ciudadlimpia-production.up.railway.app/"



    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        // En producción cambiar a Level.NONE para no loguear datos sensibles
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

    /*private val retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }*/

    /*val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }


}*/
package com.nickrodriguez.ciudadlimpia.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

object CloudinaryService {

    private val client = OkHttpClient()
    private const val CLOUD_NAME = "difmp0gnp"
    private const val UPLOAD_PRESET = "ml_default"

    fun subirFoto(
        archivo: File,
        onExito: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                archivo.name,
                archivo.asRequestBody("image/*".toMediaType())
            )
            .addFormDataPart("upload_preset", UPLOAD_PRESET)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    val url = JSONObject(body).getString("secure_url")
                    onExito(url)
                } else {
                    onError("Error subiendo foto: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                onError("Error de red: ${e.message}")
            }
        })
    }
}
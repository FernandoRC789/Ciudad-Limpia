package com.nickrodriguez.ciudadlimpia.ui.admin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import java.util.concurrent.TimeUnit

data class ReporteActivoAdmin(
    val reporteId: Long,
    val latitud: Double,
    val longitud: Double,
    val estado: String,
    val titulo: String,
    val evento: String?
)

interface MapaAdminApiService {
    @GET("api/mapa/reportes-activos")
    fun getReportesActivos(
        @Header("Authorization") token: String
    ): Call<List<ReporteActivoAdmin>>
}

class MapaAdminActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var tvEstado: TextView
    private lateinit var tvContador: TextView
    private val marcadores = mutableMapOf<Long, Marker>()
    private val gson = Gson()
    private var webSocket: WebSocket? = null

    private val BASE_URL = "https://ciudadlimpia-production.up.railway.app/"
    private val WS_URL = "wss://ciudadlimpia-production.up.railway.app/ws/mapa"

    override fun onCreate(savedInstanceState: Bundle?) {
        Configuration.getInstance().userAgentValue = packageName
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_admin)

        mapView = findViewById(R.id.mapView)
        tvEstado = findViewById(R.id.tvEstado)
        tvContador = findViewById(R.id.tvContador)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(12.0)
        mapView.controller.setCenter(GeoPoint(-12.0464, -77.0428)) // Lima

        cargarReportesActivos()
        conectarWebSocket()
    }

    private fun cargarReportesActivos() {
        val token = SessionManager(this).getToken() ?: return

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MapaAdminApiService::class.java)
            .getReportesActivos("Bearer $token")
            .enqueue(object : Callback<List<ReporteActivoAdmin>> {
                override fun onResponse(
                    call: Call<List<ReporteActivoAdmin>>,
                    response: retrofit2.Response<List<ReporteActivoAdmin>>
                ) {
                    if (response.isSuccessful) {
                        val reportes = response.body() ?: emptyList()
                        runOnUiThread {
                            reportes.forEach { agregarOActualizarMarcador(it) }
                            actualizarContador()
                        }
                    }
                }

                override fun onFailure(call: Call<List<ReporteActivoAdmin>>, t: Throwable) {
                    runOnUiThread { tvEstado.text = "Error de red" }
                }
            })
    }

    private fun conectarWebSocket() {
        val token = SessionManager(this).getToken() ?: return

        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder()
            .url(WS_URL)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(ws: WebSocket, response: Response) {
                val connectFrame = "CONNECT\n" +
                        "Authorization:Bearer $token\n" +
                        "accept-version:1.2\n" +
                        "heart-beat:0,0\n\n\u0000"
                ws.send(connectFrame)
            }

            override fun onMessage(ws: WebSocket, text: String) {
                runOnUiThread {
                    when {
                        text.startsWith("CONNECTED") -> {
                            tvEstado.text = "✅ En vivo"
                            val subscribeFrame = "SUBSCRIBE\n" +
                                    "id:sub-0\n" +
                                    "destination:/topic/mapa\n\n\u0000"
                            ws.send(subscribeFrame)
                        }
                        text.startsWith("MESSAGE") -> {
                            try {
                                val partes = text.split("\n\n")
                                if (partes.size >= 2) {
                                    val body = partes[1].replace("\u0000", "").trim()
                                    val evento = gson.fromJson(body, ReporteActivoAdmin::class.java)
                                    when (evento.evento) {
                                        "REPORTE_NUEVO" -> {
                                            agregarOActualizarMarcador(evento)
                                            actualizarContador()
                                        }
                                        "ESTADO_ACTUALIZADO" -> {
                                            if (evento.estado == "ATENDIDO" || evento.estado == "RECHAZADO") {
                                                eliminarMarcador(evento.reporteId)
                                            } else {
                                                agregarOActualizarMarcador(evento)
                                            }
                                            actualizarContador()
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                // ignorar mensajes no parseables
                            }
                        }
                        text.startsWith("ERROR") -> {
                            tvEstado.text = "❌ Error WebSocket"
                        }
                    }
                }
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                runOnUiThread { tvEstado.text = "❌ Desconectado" }
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                runOnUiThread { tvEstado.text = "Desconectado" }
            }
        })
    }

    private fun agregarOActualizarMarcador(reporte: ReporteActivoAdmin) {
        marcadores[reporte.reporteId]?.let { mapView.overlays.remove(it) }

        val marker = Marker(mapView)
        marker.position = GeoPoint(reporte.latitud, reporte.longitud)
        marker.title = reporte.titulo
        marker.snippet = "Estado: ${reporte.estado} | ID: #${reporte.reporteId}"

        mapView.overlays.add(marker)
        marcadores[reporte.reporteId] = marker
        mapView.invalidate()
    }

    private fun eliminarMarcador(reporteId: Long) {
        marcadores[reporteId]?.let {
            mapView.overlays.remove(it)
            marcadores.remove(reporteId)
            mapView.invalidate()
        }
    }

    private fun actualizarContador() {
        tvContador.text = "${marcadores.size} reportes activos"
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket?.close(1000, "Activity destruida")
    }
}
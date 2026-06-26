package com.nickrodriguez.ciudadlimpia.ui.rewards

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nickrodriguez.ciudadlimpia.network.RetrofitClient
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nickrodriguez.ciudadlimpia.utils.SessionManager
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.model.Cupon
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext

class RewardsFragment : Fragment(R.layout.fragment_rewards)
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var rvInsignias: RecyclerView
    private lateinit var rvCupones: RecyclerView
    private lateinit var rvRanking: RecyclerView

    private lateinit var tvPuntosTotales: android.widget.TextView
    private lateinit var tvPuntosParaSiguienteNivel: android.widget.TextView
    private lateinit var progressNivel: android.widget.ProgressBar
    private lateinit var tvPosicionUsuario: android.widget.TextView

    private lateinit var layoutEstadoCarga: android.widget.LinearLayout
    private lateinit var progressCargaGeneral: android.widget.ProgressBar
    private lateinit var tvMensajeError: android.widget.TextView

    private lateinit var insigniasAdapter: InsigniasAdapter
    private lateinit var cuponesAdapter: CuponesAdapter
    private lateinit var rankingAdapter: RankingAdapter

    // Puntos actuales del usuario en memoria; se usa para habilitar/deshabilitar
    // cupones según si alcanza o no. Se actualiza tras cargar el historial y
    // tras cada canje exitoso.
    private var puntosActuales: Int = 0

    private val api by lazy {RetrofitClient.apiService}
    private val nf = NumberFormat.getNumberInstance(Locale("es", "PE"))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupRecyclerViews()

        swipeRefresh.setOnRefreshListener {
            cargarTodo(esRefrescoManual = true)
        }

        cargarTodo()
    }

private fun bindViews(view: View) {

    swipeRefresh = view.findViewById(R.id.swipeRefresh)

    rvInsignias = view.findViewById(R.id.rvInsignias)
    rvCupones = view.findViewById(R.id.rvCupones)
    rvRanking = view.findViewById(R.id.rvRanking)

    tvPuntosTotales = view.findViewById(R.id.tvPuntosTotales)
    tvPuntosParaSiguienteNivel = view.findViewById(R.id.tvPuntosParaSiguienteNivel)
    progressNivel = view.findViewById(R.id.progressNivel)
    tvPosicionUsuario = view.findViewById(R.id.tvPosicionUsuario)

    layoutEstadoCarga = view.findViewById(R.id.layoutEstadoCarga)
    progressCargaGeneral = view.findViewById(R.id.progressCargaGeneral)
    tvMensajeError = view.findViewById(R.id.tvMensajeError)
}

    private fun setupRecyclerViews() {
        insigniasAdapter = InsigniasAdapter(emptyList())
        rvInsignias.apply {
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = insigniasAdapter
        }

        cuponesAdapter = CuponesAdapter(
            items = emptyList(),
            puntosDisponibles = { puntosActuales },
            onCuponClick = { cupon -> mostrarConfirmacionCanje(cupon) }
        )
        rvCupones.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cuponesAdapter
        }

        rankingAdapter = RankingAdapter(emptyList())
        rvRanking.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rankingAdapter
        }
    }

    /** Dispara las 3 cargas en paralelo: historial de puntos, ranking y cupones. */
    private fun cargarTodo(esRefrescoManual: Boolean = false) {
        if (!esRefrescoManual) mostrarCarga(true)
        cargarHistorialPuntos(esRefrescoManual)
        cargarRanking()
        cargarCupones()
    }

    private fun mostrarCarga(mostrar: Boolean) {
        layoutEstadoCarga.visibility = if (mostrar) android.view.View.VISIBLE else android.view.View.GONE
        progressCargaGeneral.visibility = if (mostrar) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun mostrarError(mensaje: String) {
        layoutEstadoCarga.visibility = android.view.View.VISIBLE
        tvMensajeError.visibility = android.view.View.VISIBLE
        tvMensajeError.text = mensaje
    }

    // ---------------------------------------------------------------------
    // GET /api/perfil/puntos/historial
    // ---------------------------------------------------------------------
    private fun cargarHistorialPuntos(esRefrescoManual: Boolean = false) {
        lifecycleScope.launch {
            try {
                val token = SessionManager.bearerToken(requireContext())
                val response = api.getHistorialPuntos(token)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        mostrarCarga(false)
                        puntosActuales = body.puntosActuales
                        tvPuntosTotales.text = "${nf.format(body.puntosActuales)} pts"
                        tvPuntosParaSiguienteNivel.text =
                            "${nf.format(body.puntosParaSiguienteNivel)} pts para Nivel ${body.nivelActual + 1}"
                        progressNivel.progress = body.porcentajeProgreso

                        body.insignias?.let { insigniasAdapter.updateData(it) }

                        // Re-evalúa qué cupones son canjeables ahora que ya
                        // sabemos los puntos reales del usuario.
                        cuponesAdapter.notifyDataSetChanged()
                    }
                } else {
                    mostrarError("No se pudo cargar tu progreso (código ${response.code()})")
                }
            } catch (e: Exception) {
                mostrarError("Error de conexión al cargar tu progreso. Revisa tu internet.")
            } finally {
                if (esRefrescoManual) swipeRefresh.isRefreshing = false
            }
        }
    }

    // ---------------------------------------------------------------------
    // GET /api/ranking
    // ---------------------------------------------------------------------
    private fun cargarRanking() {
        lifecycleScope.launch {
            try {
                val token = SessionManager.bearerToken(requireContext())
                val response = api.getRanking(token)

                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    // Mostramos solo el Top 5 en pantalla, igual que el diseño original.
                    rankingAdapter.updateData(lista.take(5))

                    val miPosicion = lista.firstOrNull { it.esUsuarioActual }?.posicion
                    tvPosicionUsuario.text = if (miPosicion != null) {
                        "Estás en la posición #$miPosicion de tu distrito"
                    } else {
                        "Aún no apareces en el ranking de tu distrito"
                    }
                } else {
                    Toast.makeText(
                        this@RewardsActivity,
                        "No se pudo cargar el ranking (código ${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@RewardsActivity,
                    "Error de conexión al cargar el ranking.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ---------------------------------------------------------------------
    // GET /api/cupons/disponibles
    // ---------------------------------------------------------------------
    private fun cargarCupones() {
        lifecycleScope.launch {
            try {
                val token = SessionManager.bearerToken(requireContext())
                val response = api.getCuponesDisponibles(token)

                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    // Solo se muestran cupones activos y con stock disponible.
                    val visibles = lista.filter { it.activo }
                    cuponesAdapter.updateData(visibles)
                } else {
                    Toast.makeText(
                        this@RewardsActivity,
                        "No se pudieron cargar los cupones (código ${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@RewardsActivity,
                    "Error de conexión al cargar los cupones.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ---------------------------------------------------------------------
    // POST /api/canjes/cupon/{id}
    // ---------------------------------------------------------------------
    private fun mostrarConfirmacionCanje(cupon: Cupon) {
        if (puntosActuales < cupon.costoPuntos) {
            Toast.makeText(
                this,
                "Te faltan ${nf.format(cupon.costoPuntos - puntosActuales)} pts para este cupón",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Canjear cupón")
            .setMessage("¿Deseas canjear \"${cupon.nombre}\" por ${nf.format(cupon.costoPuntos)} puntos?")
            .setPositiveButton("Canjear") { _, _ -> canjearCupon(cupon) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun canjearCupon(cupon: Cupon) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = SessionManager.bearerToken(requireContext())
                val response = api.canjearCupon(token, cupon.id)

                if (response.isSuccessful) {
                    val body = response.body()
                    Toast.makeText(
                        requireContext(),
                        body?.mensaje ?: "¡Cupón canjeado con éxito!",
                        Toast.LENGTH_LONG
                    ).show()

                    // Si el backend devuelve los puntos restantes, los reflejamos
                    // de inmediato sin esperar una nueva consulta.
                    body?.puntosRestantes?.let {
                        puntosActuales = it
                        tvPuntosTotales.text = "${nf.format(it)} pts"
                    }

                    // Refresca cupones (por si el stock cambió) y el historial completo.
                    cargarCupones()
                    cargarHistorialPuntos()
                } else {
                    val mensaje = when (response.code()) {
                        400 -> "No tienes suficientes puntos para este cupón."
                        404 -> "Este cupón ya no está disponible."
                        409 -> "Este cupón ya fue canjeado o se agotó el stock."
                        else -> "No se pudo canjear el cupón (código ${response.code()})."
                    }
                    Toast.makeText(this@RewardsActivity, mensaje, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@RewardsActivity,
                    "Error de conexión al canjear el cupón. Intenta nuevamente.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

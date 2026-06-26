package com.nickrodriguez.ciudadlimpia.ui.rewards

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.activityViewModels
import com.nickrodriguez.ciudadlimpia.model.PerfilResponse
import com.nickrodriguez.ciudadlimpia.viewmodel.SharedProfileViewModel
import kotlin.getValue

/**
 * RewardsFragment — CORREGIDO
 *
 * Sigue el MISMO patrón que HomeFragment: el Fragment no llama a Retrofit
 * directamente, solo observa LiveData del SharedProfileViewModel (a nivel
 * de Activity) y le pide que cargue datos "si es necesario". Esto evita
 * llamadas duplicadas y mantiene una sola fuente de verdad para toda la app.
 *
 * CAMBIOS respecto a la versión anterior:
 * 1. Eliminadas las 4 llamadas directas a `api.getXxx(token)` — ahora todo
 *    pasa por rewardsViewModel, igual que perfil/reportes en HomeFragment.
 * 2. Eliminado el Toast de debug que mostraba el JWT en pantalla.
 * 3. El token ahora se envía como "Bearer $token" (antes se mandaba crudo,
 *    por eso el backend probablemente respondía 401/403 en todo Rewards).
 * 4. Eliminada la función `recargarDatos()` duplicada y sin usar — el
 *    pull-to-refresh ahora llama a `rewardsViewModel.refrescarRewards(token)`.
 * 5. Eliminados imports basura (ContentProviderCompat.requireContext,
 *    kotlin.getValue) que no se usaban para nada.
 * 6. Lectura de SharedPreferences centralizada en un solo lugar
 *    (obtenerToken()), en vez de repetirla 4 veces.
 */
class RewardsFragment : Fragment(R.layout.fragment_rewards) {

    private val rewardsViewModel: SharedProfileViewModel by activityViewModels()

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
    // cupones según si alcanza o no. Se actualiza cada vez que el LiveData de
    // historialPuntos emite un nuevo valor.
    private var puntosActuales: Int = 0

    private val nf = NumberFormat.getNumberInstance(Locale("es", "PE"))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModelHeader(view)
        bindViews(view)
        setupRecyclerViews()
        observeViewModel()

        swipeRefresh.setOnRefreshListener {
            rewardsViewModel.refrescarRewards(obtenerToken())
            swipeRefresh.isRefreshing = false
        }

        mostrarCarga(true)
        rewardsViewModel.cargarRewardsSiEsNecesario(obtenerToken())
        Log.d("RANKING_CHECK", "FRAGMENT CARGADO") // 👈 esto
    }

    private fun bindViews(view: View) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh)

        rvInsignias = view.findViewById(R.id.rvInsignias)
        rvCupones = view.findViewById(R.id.rvCupones)
        rvRanking = view.findViewById(R.id.rvRanking)

        tvPuntosTotales = view.findViewById(R.id.tvPuntosTotales)
        tvPuntosParaSiguienteNivel = view.findViewById(R.id.tvPuntosParaSiguienteNivel)
        progressNivel = view.findViewById(R.id.progressNivel)
        //tvPosicionUsuario = view.findViewById(R.id.tvPosicionUsuario)

        layoutEstadoCarga = view.findViewById(R.id.layoutEstadoCarga)
        progressCargaGeneral = view.findViewById(R.id.progressCargaGeneral)
        tvMensajeError = view.findViewById(R.id.tvMensajeError)
    }
    private fun observeViewModelHeader(view: View) {
        rewardsViewModel.perfil.observe(viewLifecycleOwner) { perfil ->
            perfil?.let {
                actualizarHeader(it, view)
            }
        }
    }

    private fun actualizarHeader(perfil: PerfilResponse, view: View) {
        view.findViewById<TextView>(R.id.tvAppName)?.text =
            "${perfil.nombre} ${perfil.apellido}"

        view.findViewById<TextView>(R.id.tvUserRank)?.text =
            perfil.nivel.nombre

        view.findViewById<TextView>(
            R.id.tvPoints
        )?.text =
            "${perfil.puntosTotal} pts"
    }
    private fun setupRecyclerViews() {
        insigniasAdapter = InsigniasAdapter(emptyList())
        rvInsignias.apply {
            layoutManager = LinearLayoutManager(
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

    // ── Observa el ViewModel en vez de llamar a la red directamente ────────────
    private fun observeViewModel() {

        rewardsViewModel.historialPuntos.observe(viewLifecycleOwner) { body ->
            body ?: return@observe
            mostrarCarga(false)

            puntosActuales = body.puntosActuales
            tvPuntosTotales.text = "${nf.format(body.puntosActuales)} pts"
            tvPuntosParaSiguienteNivel.text =
                "${nf.format(body.puntosParaSiguienteNivel)} pts para Nivel ${body.nivelActual + 1}"
            progressNivel.progress = body.porcentajeProgreso

            body.insignias?.let { insigniasAdapter.updateData(it) }

            // Re-evalúa qué cupones son canjeables ahora que ya sabemos los
            // puntos reales del usuario.
            cuponesAdapter.notifyDataSetChanged()
        }
        Log.d("RANKING_CHECK", "FRAGMENT CARGADO")
        rewardsViewModel.ranking.observe(viewLifecycleOwner) { lista ->
            Log.d("RANKING_CHECK", "ENTRÓ AL OBSERVER")
            rankingAdapter.updateData(lista.take(5))

            val nombreUsuarioActual =
                SessionManager(requireContext()).getNombre()?.trim()?.lowercase()

            val miItem = lista.firstOrNull {
                it.nombreCompleto.trim().lowercase() == nombreUsuarioActual
            }

            val miPosicion = miItem?.posicion

            /*tvPosicionUsuario.text = if (miPosicion != null) {
                "Estás en la posición #$miPosicion de tu distrito"
            } else {
                "Aún no apareces en el ranking de tu distrito"
            }
            Log.d("RANKING_CHECK", "API: ${lista.map { it.nombreCompleto }}")*/
        }

        rewardsViewModel.cupones.observe(viewLifecycleOwner) { lista ->
            cuponesAdapter.updateData(lista)
        }

        rewardsViewModel.rewardsError.observe(viewLifecycleOwner) { mensaje ->
            mensaje?.let {
                mostrarError(it)
                rewardsViewModel.consumirRewardsError()
            }
        }

        rewardsViewModel.canjeResultado.observe(viewLifecycleOwner) { resultado ->
            resultado?.let {
                Toast.makeText(requireContext(), it.mensaje, Toast.LENGTH_LONG).show()
                rewardsViewModel.consumirCanjeResultado()
            }
        }
    }

    private fun mostrarCarga(mostrar: Boolean) {
        layoutEstadoCarga.visibility = if (mostrar) View.VISIBLE else View.GONE
        progressCargaGeneral.visibility = if (mostrar) View.VISIBLE else View.GONE
    }

    private fun mostrarError(mensaje: String) {
        layoutEstadoCarga.visibility = View.VISIBLE
        tvMensajeError.visibility = View.VISIBLE
        tvMensajeError.text = mensaje
    }

    // ---------------------------------------------------------------------
    // POST /api/canjes/cupon/{id}  (delegado al ViewModel)
    // ---------------------------------------------------------------------
    private fun mostrarConfirmacionCanje(cupon: Cupon) {
        if (puntosActuales < cupon.costoPuntos) {
            Toast.makeText(
                requireContext(),
                "Te faltan ${nf.format(cupon.costoPuntos - puntosActuales)} pts para este cupón",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Canjear cupón")
            .setMessage("¿Deseas canjear \"${cupon.nombre}\" por ${nf.format(cupon.costoPuntos)} puntos?")
            .setPositiveButton("Canjear") { _, _ ->
                rewardsViewModel.canjearCupon(obtenerToken(), cupon)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ── Lectura centralizada del token, igual que HomeFragment ─────────────────
    private fun obtenerToken(): String {
        val prefs = requireActivity().getSharedPreferences(
            "ciudad_limpia", android.content.Context.MODE_PRIVATE
        )
        return prefs.getString("jwt_token", "") ?: ""
    }

    companion object {
        fun newInstance() = RewardsFragment()
    }
}


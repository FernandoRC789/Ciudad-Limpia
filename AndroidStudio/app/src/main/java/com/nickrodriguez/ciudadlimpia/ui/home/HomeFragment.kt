package com.nickrodriguez.ciudadlimpia.ui.home

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.adapter.reporte.MisReportesAdapter
import com.nickrodriguez.ciudadlimpia.ui.reporte.ReporteFragment
import com.nickrodriguez.ciudadlimpia.model.PerfilResponse
import com.nickrodriguez.ciudadlimpia.network.RetrofitClient
import kotlinx.coroutines.launch
import android.net.Uri
import android.widget.ImageView
import java.io.File
import com.nickrodriguez.ciudadlimpia.viewmodel.SharedProfileViewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class HomeFragment : Fragment() {

    private val profileViewModel: SharedProfileViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MisReportesAdapter
    private var progressPercent = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onViewCreated(view, savedInstanceState)

        /*recyclerView = view.findViewById(R.id.rvRecentReports)

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())*/

        adapter = MisReportesAdapter(
            mutableListOf()
        )

        /*recyclerView.adapter = adapter*/

        val swipeRefresh =
            view.findViewById<SwipeRefreshLayout>(
                R.id.swipeRefresh
            )

        /*swipeRefresh.setOnRefreshListener {

            //cargarPerfil(view)
            //cargarReportes()


            profileViewModel.refrescarDatos(
                token,
                usuarioId
            )
            swipeRefresh.isRefreshing = false        }*/

        swipeRefresh.setOnRefreshListener {

            recargarDatos(
                view,
                swipeRefresh
            )
        }

        //cargarPerfil(view)
        //cargarReportes()
        setupRecyclerView(view)
        observeViewModel(view)
        setupClickListeners(view)
        triggerDataLoad()
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.rvRecentReports)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MisReportesAdapter(mutableListOf())
        recyclerView.adapter = adapter
    }

    /*private fun recargarDatos(
        view: View,
        swipeRefresh: SwipeRefreshLayout
    ) {
        lifecycleScope.launch {

            cargarPerfil(view)
            cargarReportes()

            swipeRefresh.isRefreshing = false
        }
    }*/
    private fun recargarDatos(
        view: View,
        swipeRefresh: SwipeRefreshLayout
    ) {

        val prefs =
            requireActivity().getSharedPreferences(
                "ciudad_limpia",
                android.content.Context.MODE_PRIVATE
            )

        val token =
            prefs.getString(
                "jwt_token",
                ""
            ) ?: ""

        val usuarioId =
            prefs.getLong(
                "usuario_id",
                0
            )

        profileViewModel.refrescarDatos(
            token,
            usuarioId
        )

        swipeRefresh.isRefreshing = false
    }

    // ── Observa el ViewModel en vez de llamar a la red directamente ────────────
    private fun observeViewModel(view: View) {
        profileViewModel.perfil.observe(viewLifecycleOwner) { perfil ->
            perfil?.let { mostrarPerfil(view, it) }
        }

        profileViewModel.reportes.observe(viewLifecycleOwner) { reportes ->
            adapter.actualizarLista(reportes)
        }

        profileViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    // ── Pide datos SOLO si no están ya cargados (ver ViewModel) ─────────────────
    private fun triggerDataLoad() {
        val prefs = requireActivity().getSharedPreferences(
            "ciudad_limpia", android.content.Context.MODE_PRIVATE
        )
        val token = prefs.getString("jwt_token", "") ?: ""
        val usuarioId = prefs.getLong("usuario_id", 0)

        profileViewModel.cargarPerfilSiEsNecesario(token, usuarioId)
        profileViewModel.cargarReportesSiEsNecesario(token)
    }
    /*private fun cargarReportes() {

        lifecycleScope.launch {

            try {
                val prefs = requireActivity()
                    .getSharedPreferences(
                        "ciudad_limpia",
                        android.content.Context.MODE_PRIVATE
                    )

                val token =
                    prefs.getString(
                        "jwt_token",
                        ""
                    ) ?: ""

                val response =
                    RetrofitClient.apiService
                        .getMisReportes(
                            "Bearer $token"
                        )

                if(response.isSuccessful){

                    val reportes =
                        response.body() ?: emptyList()

                    adapter.actualizarLista(
                        reportes
                    )
                }

            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    requireContext(),
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }*/

    // ── Barra de progreso animada ─────────────────────────────────────────────
    private fun setupProgressBar(view: View) {
        val fillView = view.findViewById<View>(R.id.viewProgressFill) ?: return
        fillView.post {
            val parentWidth = (fillView.parent as? View)?.width ?: return@post
            val targetWidth = (parentWidth * progressPercent).toInt()
            ValueAnimator.ofInt(0, targetWidth).apply {
                duration     = 900L
                startDelay   = 400L
                interpolator = DecelerateInterpolator(1.5f)
                addUpdateListener { anim ->
                    fillView.layoutParams = fillView.layoutParams.also {
                        it.width = anim.animatedValue as Int
                    }
                }
                start()
            }
        }
    }
    // ── Click listeners ───────────────────────────────────────────────────────
    private fun setupClickListeners(view: View) {
        view.findViewById<MaterialButton>(R.id.btnReportIncident)
            ?.setOnClickListener {

                parentFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.fragmentContainer,
                        ReporteFragment()
                    )
                    .addToBackStack(null)
                    .commit()
            }

        view.findViewById<MaterialButton>(R.id.btnShareAchievements)
            ?.setOnClickListener { shareAchievements() }
        view.findViewById<TextView>(R.id.tvSeeAll)
            ?.setOnClickListener { navigateToAllReports() }
        view.findViewById<android.widget.ImageButton>(R.id.btnNotifications)
            ?.setOnClickListener { navigateToNotifications() }
    }

    // ── Navegación ────────────────────────────────────────────────────────────
    private fun navigateToNewReport() {
        // findNavController().navigate(R.id.action_home_to_newReport)
        Toast.makeText(requireContext(), "Nuevo reporte", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToReportDetail(reportId: String) {
        // findNavController().navigate(HomeFragmentDirections.actionHomeToReportDetail(reportId))
        Toast.makeText(requireContext(), "Reporte: $reportId", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToAllReports() {
        // findNavController().navigate(R.id.action_home_to_allReports)
        Toast.makeText(requireContext(), "Ver todos los reportes", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToNotifications() {
        Toast.makeText(requireContext(), "Notificaciones", Toast.LENGTH_SHORT).show()
    }
    private fun shareAchievements() {

        val nivel =
            requireView().findViewById<TextView>(
                R.id.tvUserRank
            ).text

        val puntos =
            requireView().findViewById<TextView>(
                R.id.tvPoints
            ).text

        val texto =
            """
🏆 Estoy ayudando a mantener limpia mi ciudad con Ciudad Limpia.

⭐ Nivel: $nivel
🎯 Puntos: $puntos

¡Juntos podemos construir una ciudad mejor!
        """.trimIndent()

        val intent = Intent().apply {

            action = Intent.ACTION_SEND

            putExtra(
                Intent.EXTRA_TEXT,
                texto
            )

            type = "text/plain"
        }

        startActivity(
            Intent.createChooser(
                intent,
                "Compartir logro"
            )
        )
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    /*private fun cargarPerfil(view: View) {
        android.util.Log.e(
            "HOME_TEST",
            "Entró a cargarPerfil"
        )

        lifecycleScope.launch {
            try {
                val prefs = requireActivity()
                    .getSharedPreferences(
                        "ciudad_limpia",
                        android.content.Context.MODE_PRIVATE
                    )
                val usuarioId =
                    prefs.getLong(
                        "usuario_id",
                        0
                    )
                android.util.Log.e(
                    "HOME_TEST",
                    "usuarioId=$usuarioId"
                )
                val token =
                    prefs.getString(
                        "jwt_token",
                        ""
                    ) ?: ""
                val response =
                    RetrofitClient
                        .apiService
                        .getPerfil(
                            "Bearer $token",
                            //usuarioId
                        )
                android.util.Log.e(
                    "HOME_TEST",
                    "responseCode=${response.code()}"
                )
                android.util.Log.e(
                    "HOME_TEST",
                    "isSuccessful=${response.isSuccessful}"
                )
                if(response.isSuccessful){
                    android.util.Log.e(
                        "HOME_TEST",
                        "body=${response.body()}"
                    )
                    val perfil =
                        response.body() ?: return@launch
                    mostrarPerfil(
                        view,
                        perfil
                    )
                }
            }catch (e: Exception){
                Toast.makeText(
                    requireContext(),
                    e.message ?: "Error desconocido",
                    Toast.LENGTH_LONG
                ).show()
                android.util.Log.e(
                    "HOME_TEST",
                    "ERROR=${e.message}"
                )
                e.printStackTrace()
            }
        }
    }*/

    private fun mostrarPerfil(
        view: View,
        perfil: PerfilResponse
    ) {
        view.findViewById<TextView>(
            R.id.tvAppName
        )?.text =
            "${perfil.nombre} ${perfil.apellido}"
        view.findViewById<TextView>(
            R.id.tvUserRank
        )?.text =
            perfil.nivel.nombre

        view.findViewById<TextView>(
            R.id.tvLevelBadge
        )?.text = "LVL ${perfil.nivel.id}"

        view.findViewById<TextView>(
            R.id.tvPoints
        )?.text =
            "${perfil.puntosTotal} pts"
        //cargar foto del usuario
        val imgAvatar =
            view.findViewById<ImageView>(
                R.id.imgAvatar
            )

        if (!perfil.fotoPerfil.isNullOrEmpty()) {
            val ruta =
                perfil.fotoPerfil
                    .replace("\"", "")

            val file = File(ruta)

            if (file.exists()) {

                imgAvatar.setImageURI(
                    Uri.fromFile(file)
                )
            }
        }

        view.findViewById<TextView>(
            R.id.tvStatResueltos
        )?.text =
            perfil.reportesAtendidos.toString()

        view.findViewById<TextView>(
            R.id.tvStatComunidad
        )?.text =
            perfil.totalReportes.toString()

        //mostrar el nivel que sigue
        view.findViewById<TextView>(
            R.id.tvNextLevelLabel
        )?.text =
            "PRÓXIMO NIVEL: ${perfil.siguienteNivel}"

        progressPercent =
            (
                    perfil.puntosTotal.toFloat() /
                            perfil.nivel.puntosParaSiguienteNivel.toFloat()
                    ).coerceIn(0f, 1f)

        val porcentaje =
            (progressPercent * 100).toInt()

        val mensajeMotivacional = when {

            porcentaje < 20 ->
                "🌱 Cada reporte ayuda a mejorar la ciudad."

            porcentaje < 40 ->
                "🧹 Tu compromiso ya está generando impacto."

            porcentaje < 60 ->
                "🚮 La comunidad agradece tu participación."

            porcentaje < 80 ->
                "🌎 Estás contribuyendo a una ciudad más limpia."

            porcentaje < 95 ->
                "🏅 Estás muy cerca de alcanzar un nuevo nivel."

            else ->
                "🎉 ¡Solo falta un poco más para convertirte en ${perfil.siguienteNivel}!"
        }

        view.findViewById<TextView>(
            R.id.tvProgressPercent
        )?.text = "$porcentaje%"

        view.findViewById<TextView>(
            R.id.tvMotivationalTitle
        )?.text = mensajeMotivacional

        android.util.Log.e(
            "PROGRESO_TEST",
            "puntosTotal=${perfil.puntosTotal} - puntosParaSiguienteNivel=${perfil.nivel.puntosParaSiguienteNivel}"
        )

        android.util.Log.e(
            "PROGRESO_TEST",
            "progressPercent=$progressPercent"
        )

        setupProgressBar(view)
    }
}
package com.nickrodriguez.ciudadlimpia.ui.home

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.model.RecentReport
import com.nickrodriguez.ciudadlimpia.model.ReportStatus
import com.nickrodriguez.ciudadlimpia.ui.reporte.ReporteFragment
import com.nickrodriguez.ciudadlimpia.adapter.reporte.RecentReportsAdapter
// Alias para evitar import ambiguo
//private typealias ViewGroup = android.view.ViewGroup

// ─────────────────────────────────────────────────────────────────────────────
// FRAGMENT
// ─────────────────────────────────────────────────────────────────────────────

class HomeFragment : Fragment() {

    // En producción, estos datos vendrían de un ViewModel / repositorio
    private val userPoints     = 2450
    private val progressPercent = 0.85f   // 85%
    private val statResueltos  = 12
    private val statComunidad  = 48

    private val sampleReports = listOf(
        RecentReport(
            id = "1",
            title = "Basura en Av. Central",
            meta = "Hace 2 horas · San Isidro",
            status = ReportStatus.IN_PROGRESS,
            likesCount = 14,
            imageResId = R.drawable.ic_image_placeholder
        ),
        RecentReport(
            id            = "2",
            title         = "Luz apagada",
            meta          = "Ayer · Plaza Mayor",
            status        = ReportStatus.RESOLVED,
            pointsEarned  = 50,
            imageResId    = R.drawable.ic_image_placeholder
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupStats(view)
        setupProgressBar(view)
        setupRecentReports(view)
        setupClickListeners(view)
    }

    // ── Stats ────────────────────────────────────────────────────────────────

    private fun setupStats(view: View) {
        view.findViewById<TextView>(R.id.tvStatResueltos)?.text  = statResueltos.toString()
        view.findViewById<TextView>(R.id.tvStatComunidad)?.text  = statComunidad.toString()
        view.findViewById<TextView>(R.id.tvPoints)?.text         =
            getString(R.string.points_sample)  // reemplazar con userPoints.formatPoints()
    }

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

    // ── RecyclerView de reportes ──────────────────────────────────────────────

    private fun setupRecentReports(view: View) {
        view.findViewById<RecyclerView>(R.id.rvRecentReports)?.apply {
            layoutManager       = LinearLayoutManager(requireContext())
            adapter             = RecentReportsAdapter(sampleReports) { report ->
                navigateToReportDetail(report.id)
            }
            isNestedScrollingEnabled = false
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
        Toast.makeText(requireContext(), "Compartir logros", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
package com.nickrodriguez.ciudadlimpia.adapter.reporte

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.model.ReporteResponse

class MisReportesAdapter(
    private val reportes: MutableList<ReporteResponse>
) : RecyclerView.Adapter<MisReportesAdapter.ReporteViewHolder>() {

    inner class ReporteViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val tvTitulo =
            view.findViewById<TextView>(R.id.tvReportTitle)

        val tvMeta =
            view.findViewById<TextView>(R.id.tvReportMeta)

        val tvEstado =
            view.findViewById<TextView>(R.id.tvStatusBadge)

        val chipSecondary =
            view.findViewById<LinearLayout>(R.id.chipSecondary)

        val tvChipLabel =
            view.findViewById<TextView>(R.id.tvChipLabel)

        val imgThumb =
            view.findViewById<ImageView>(R.id.imgReportThumb)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReporteViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_report_card,
                parent,
                false
            )

        return ReporteViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ReporteViewHolder,
        position: Int
    ) {

        val reporte = reportes[position]

        holder.tvTitulo.text =
            reporte.titulo

        holder.tvMeta.text =
            reporte.direccion

        holder.tvEstado.text =
            reporte.estado

        holder.imgThumb.setImageResource(
            R.drawable.ic_image_placeholder
        )

        configurarEstado(
            holder,
            reporte
        )
    }

    private fun configurarEstado(
        holder: ReporteViewHolder,
        reporte: ReporteResponse
    ) {

        holder.chipSecondary.visibility = View.GONE

        when(reporte.estado){

            "PENDIENTE" -> {

                holder.tvEstado.text = "Pendiente"

                holder.tvEstado.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.status_pending_text
                    )
                )

                holder.tvEstado.setBackgroundResource(
                    R.drawable.bg_status_pending
                )
            }

            "ATENDIDO" -> {

                holder.tvEstado.text = "Atendido"

                holder.tvEstado.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.status_in_progress_text
                    )
                )

                holder.tvEstado.setBackgroundResource(
                    R.drawable.bg_status_in_progress
                )
            }

            "RESUELTO" -> {

                holder.tvEstado.text = "Resuelto"

                holder.tvEstado.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.status_resolved_text
                    )
                )

                holder.tvEstado.setBackgroundResource(
                    R.drawable.bg_status_resolved
                )

                holder.chipSecondary.visibility =
                    View.VISIBLE

                holder.tvChipLabel.text =
                    "+${reporte.puntosOtorgados} pts"
            }
        }
    }

    override fun getItemCount() =
        reportes.size

    fun actualizarLista(
        nuevosReportes: List<ReporteResponse>
    ) {
        reportes.clear()
        reportes.addAll(nuevosReportes)
        notifyDataSetChanged()
    }
}


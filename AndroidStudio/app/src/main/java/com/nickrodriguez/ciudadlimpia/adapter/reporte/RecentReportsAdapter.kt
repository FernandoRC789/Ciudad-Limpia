package com.nickrodriguez.ciudadlimpia.adapter.reporte

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.model.RecentReport
import com.nickrodriguez.ciudadlimpia.model.ReportStatus

class RecentReportsAdapter(
    private val reports: List<RecentReport>,
    private val onItemClick: (RecentReport) -> Unit
) : RecyclerView.Adapter<RecentReportsAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgThumb: android.widget.ImageView = view.findViewById(R.id.imgReportThumb)
        val tvTitle: TextView = view.findViewById(R.id.tvReportTitle)
        val tvMeta: TextView                   = view.findViewById(R.id.tvReportMeta)
        val tvStatus: TextView                 = view.findViewById(R.id.tvStatusBadge)
        val chipSecondary: ViewGroup = view.findViewById(R.id.chipSecondary)
        val imgChipIcon: android.widget.ImageView = view.findViewById(R.id.imgChipIcon)
        val tvChipLabel: TextView              = view.findViewById(R.id.tvChipLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder =
        ReportViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_report_card, parent, false)
        )

    override fun getItemCount() = reports.size

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]
        val ctx    = holder.itemView.context

        holder.imgThumb.setImageResource(report.imageResId)
        holder.tvTitle.text = report.title
        holder.tvMeta.text  = report.meta

        bindStatusBadge(holder, report, ctx)
        bindSecondaryChip(holder, report, ctx)

        holder.itemView.setOnClickListener { onItemClick(report) }
    }

    private fun bindStatusBadge(holder: ReportViewHolder, report: RecentReport, ctx: android.content.Context) {
        when (report.status) {
            ReportStatus.IN_PROGRESS -> {
                holder.tvStatus.text = ctx.getString(R.string.status_in_progress)
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_in_progress)
                holder.tvStatus.setTextColor(ctx.getColor(R.color.status_in_progress_text))
            }
            ReportStatus.RESOLVED -> {
                holder.tvStatus.text = ctx.getString(R.string.status_resolved)
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_resolved)
                holder.tvStatus.setTextColor(ctx.getColor(R.color.status_resolved_text))
            }
        }
    }

    private fun bindSecondaryChip(holder: ReportViewHolder, report: RecentReport, ctx: android.content.Context) {
        when {
            report.status == ReportStatus.IN_PROGRESS && report.likesCount > 0 -> {
                holder.chipSecondary.visibility = View.VISIBLE
                holder.imgChipIcon.setImageResource(R.drawable.ic_thumb_up_outline)
                holder.imgChipIcon.setColorFilter(ctx.getColor(R.color.chip_likes_text))
                holder.tvChipLabel.text = ctx.getString(R.string.chip_likes, report.likesCount)
                holder.tvChipLabel.setTextColor(ctx.getColor(R.color.chip_likes_text))
            }
            report.status == ReportStatus.RESOLVED && report.pointsEarned > 0 -> {
                holder.chipSecondary.visibility = View.VISIBLE
                holder.imgChipIcon.setImageResource(R.drawable.ic_trophy)
                holder.imgChipIcon.setColorFilter(ctx.getColor(R.color.amber_600))
                holder.tvChipLabel.text = ctx.getString(R.string.chip_points_earned, report.pointsEarned)
                holder.tvChipLabel.setTextColor(ctx.getColor(R.color.chip_secondary_text))
            }
            else -> holder.chipSecondary.visibility = View.GONE
        }
    }
}
package com.nickrodriguez.ciudadlimpia.model

import com.nickrodriguez.ciudadlimpia.R

enum class ReportStatus { IN_PROGRESS, RESOLVED }

data class RecentReport(
    val id: String,
    val title: String,
    val meta: String,           // "Hace 2 horas · San Isidro"
    val status: ReportStatus,
    val likesCount: Int = 0,    // visible si IN_PROGRESS
    val pointsEarned: Int = 0,  // visible si RESOLVED
    val imageResId: Int = R.drawable.ic_image_placeholder
)
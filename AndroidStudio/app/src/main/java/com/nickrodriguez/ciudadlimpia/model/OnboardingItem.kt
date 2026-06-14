package com.nickrodriguez.ciudadlimpia.model

data class OnboardingItem(
    val imageRes: Int? = null,
    val lottieFile: String? = null,

    val title: String,
    val description: String,

    val backgroundColorRes: Int,

    val titleColorRes: Int,

    val descriptionColorRes: Int,

    val buttonBackgroundRes: Int,

    val buttonTextColorRes: Int,

    val skipTextColorRes: Int
)
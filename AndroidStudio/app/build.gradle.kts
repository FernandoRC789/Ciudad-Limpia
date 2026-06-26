plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.nickrodriguez.ciudadlimpia"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.nickrodriguez.ciudadlimpia"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.cast.framework)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Para foto de perfil circular con carga de red:
    implementation("io.coil-kt:coil:2.5.0")
    implementation("com.airbnb.android:lottie:6.4.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    //para conectar con api de backend
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //location
    implementation("com.google.android.gms:play-services-location:21.3.0")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))

    //implementation(libs.firebase.storage.ktx)

    implementation("androidx.activity:activity-ktx:1.10.1")
    // ── NUEVO: necesario para activityViewModels() y SharedProfileViewModel ────
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // Glide (avatares circulares del ranking)
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
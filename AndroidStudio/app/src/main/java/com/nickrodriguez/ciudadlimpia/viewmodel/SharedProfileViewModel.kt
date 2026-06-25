package com.nickrodriguez.ciudadlimpia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nickrodriguez.ciudadlimpia.model.PerfilResponse
import com.nickrodriguez.ciudadlimpia.model.ReporteResponse
import com.nickrodriguez.ciudadlimpia.network.RetrofitClient
import kotlinx.coroutines.launch

/**
 * SharedProfileViewModel
 *
 * PROBLEMA QUE RESUELVE:
 * Antes, HomeFragment Y ProfileFragment llamaban a getPerfil() por su cuenta,
 * cada vez que el usuario entraba a esa pantalla. Resultado: 2-3 llamadas HTTP
 * idénticas en cuestión de segundos, cada una con su propio lag.
 *
 * SOLUCIÓN:
 * Un solo ViewModel, instalado a nivel de Activity (activityViewModels()),
 * compartido por ambos Fragments. Los datos se cargan UNA VEZ y se guardan
 * en LiveData. Si ya existen datos en memoria, no se vuelve a llamar a la red
 * a menos que se pida explícitamente (pull-to-refresh, por ejemplo).
 *
 * Cómo usarlo desde un Fragment:
 *
 *   private val profileViewModel: SharedProfileViewModel by activityViewModels()
 *
 *   profileViewModel.perfil.observe(viewLifecycleOwner) { perfil ->
 *       perfil?.let { mostrarPerfil(view, it) }
 *   }
 *
 *   override fun onViewCreated(...) {
 *       profileViewModel.cargarPerfilSiEsNecesario(token, usuarioId)
 *   }
 */
class SharedProfileViewModel : ViewModel() {
    private val _perfil = MutableLiveData<PerfilResponse?>()
    val perfil: LiveData<PerfilResponse?> = _perfil
    private val _reportes = MutableLiveData<List<ReporteResponse>>()
    val reportes: LiveData<List<ReporteResponse>> = _reportes

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // ── Evita refetch si ya tenemos los datos en memoria ────────────────────────
    private var perfilCargado = false
    private var reportesCargados = false

    /**
     * Carga el perfil SOLO si no está ya en memoria.
     * Esto es lo que elimina las llamadas duplicadas entre Home y Profile.
     */
    fun cargarPerfilSiEsNecesario(token: String, usuarioId: Long) {
        if (perfilCargado) return  // ← ya lo tenemos, no repetir la llamada
        cargarPerfil(token, usuarioId)
    }

    /** Fuerza una recarga (usar en pull-to-refresh o tras editar el perfil) */
    fun refrescarPerfil(token: String, usuarioId: Long) {
        perfilCargado = false
        cargarPerfil(token, usuarioId)
    }

    private fun cargarPerfil(token: String, usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.apiService.getPerfil(
                    "Bearer $token",
                    //usuarioId
                )
                if (response.isSuccessful) {
                    _perfil.value = response.body()
                    perfilCargado = true
                } else {
                    _error.value = "Error ${response.code()} al cargar perfil"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error de conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarReportesSiEsNecesario(token: String) {
        if (reportesCargados) return
        cargarReportes(token)
    }

    fun refrescarReportes(token: String) {
        reportesCargados = false
        cargarReportes(token)
    }

    private fun cargarReportes(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMisReportes("Bearer $token")
                if (response.isSuccessful) {
                    _reportes.value = response.body() ?: emptyList()
                    reportesCargados = true
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar reportes"
            }
        }
    }

    /** Llamar tras crear un nuevo reporte, para refrescar contadores */
    fun invalidarCache() {
        perfilCargado = false
        reportesCargados = false
    }

    fun refrescarDatos(
        token: String,
        usuarioId: Long
    ) {

        refrescarPerfil(
            token,
            usuarioId
        )

        refrescarReportes(
            token
        )
    }



}
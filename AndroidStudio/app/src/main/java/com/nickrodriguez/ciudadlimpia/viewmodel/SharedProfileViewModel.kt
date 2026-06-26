package com.nickrodriguez.ciudadlimpia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nickrodriguez.ciudadlimpia.model.Cupon
import com.nickrodriguez.ciudadlimpia.model.HistorialPuntosResponse
import com.nickrodriguez.ciudadlimpia.model.PerfilResponse
import com.nickrodriguez.ciudadlimpia.model.RankingItem
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
 * compartido por todos los Fragments (Home, Profile y ahora Rewards). Los
 * datos se cargan UNA VEZ y se guardan en LiveData. Si ya existen datos en
 * memoria, no se vuelve a llamar a la red a menos que se pida explícitamente
 * (pull-to-refresh, por ejemplo).
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
 *
 * ── SECCIÓN REWARDS (NUEVO) ──────────────────────────────────────────────
 * Mismo patrón exacto que perfil/reportes: cada bloque tiene su LiveData,
 * su flag "yaCargado" para evitar refetch, un cargarXSiEsNecesario() para
 * uso normal, y un refrescarX() para pull-to-refresh o tras canjear un cupón.
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
        puntosCargados = false
        rankingCargado = false
        cuponesCargados = false
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

    // ═════════════════════════════════════════════════════════════════════
    // ── SECCIÓN REWARDS ──────────────────────────────────────────────────
    // ═════════════════════════════════════════════════════════════════════

    private val _historialPuntos = MutableLiveData<HistorialPuntosResponse?>()
    val historialPuntos: LiveData<HistorialPuntosResponse?> = _historialPuntos

    private val _ranking = MutableLiveData<List<RankingItem>>()
    val ranking: LiveData<List<RankingItem>> = _ranking

    private val _cupones = MutableLiveData<List<Cupon>>()
    val cupones: LiveData<List<Cupon>> = _cupones

    // Eventos de un solo uso (Toast / diálogo). Se modelan como LiveData de
    // un mensaje que el Fragment consume y limpia, para que no se repita el
    // Toast si la pantalla rota o el LiveData se vuelve a observar.
    private val _rewardsError = MutableLiveData<String?>()
    val rewardsError: LiveData<String?> = _rewardsError

    private val _canjeResultado = MutableLiveData<CanjeResultado?>()
    val canjeResultado: LiveData<CanjeResultado?> = _canjeResultado

    private val _rewardsLoading = MutableLiveData(false)
    val rewardsLoading: LiveData<Boolean> = _rewardsLoading

    private var puntosCargados = false
    private var rankingCargado = false
    private var cuponesCargados = false

    // ---------------------------------------------------------------------
    // GET /api/perfil/puntos/historial
    // ---------------------------------------------------------------------
    fun cargarPuntosSiEsNecesario(token: String) {
        if (puntosCargados) return
        cargarPuntos(token)
    }

    fun refrescarPuntos(token: String) {
        puntosCargados = false
        cargarPuntos(token)
    }

    private fun cargarPuntos(token: String) {
        viewModelScope.launch {
            try {
                val response =
                    RetrofitClient.apiService.getHistorialPuntos("Bearer $token")

                if (response.isSuccessful) {

                    val lista = response.body() ?: emptyList()

                    val total = lista.sumOf { it.puntos }

                    val fakeResponse = HistorialPuntosResponse(
                        puntosActuales = total,
                        nivelActual = 1,
                        puntosParaSiguienteNivel = 500, // temporal o calculado
                        porcentajeProgreso = ((total % 500) * 100 / 500),
                        insignias = emptyList(),
                        historial = lista
                    )

                    _historialPuntos.value = fakeResponse
                    puntosCargados = true

                } else {
                    _rewardsError.value =
                        "No se pudo cargar tu progreso (código ${response.code()})"
                }

            } catch (e: Exception) {
                _rewardsError.value =
                    "Error de conexión al cargar tu progreso. Revisa tu internet."
            }
        }
    }

    /*private fun cargarPuntos(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getHistorialPuntos("Bearer $token")
                if (response.isSuccessful) {
                    _historialPuntos.value = response.body()
                    puntosCargados = true
                } else {
                    _rewardsError.value = "No se pudo cargar tu progreso (código ${response.code()})"
                }
            } catch (e: Exception) {
                _rewardsError.value = "Error de conexión al cargar tu progreso. Revisa tu internet."
            }
        }
    }*/

    // ---------------------------------------------------------------------
    // GET /api/ranking
    // ---------------------------------------------------------------------
    fun cargarRankingSiEsNecesario(token: String) {
        if (rankingCargado) return
        cargarRanking(token)
    }

    fun refrescarRanking(token: String) {
        rankingCargado = false
        cargarRanking(token)
    }

    private fun cargarRanking(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getRanking("Bearer $token")
                if (response.isSuccessful) {
                    _ranking.value = response.body() ?: emptyList()
                    rankingCargado = true
                } else {
                    _rewardsError.value = "No se pudo cargar el ranking (código ${response.code()})"
                }
            } catch (e: Exception) {
                _rewardsError.value = "Error de conexión al cargar el ranking."
            }
        }
    }

    // ---------------------------------------------------------------------
    // GET /api/cupons/disponibles
    // ---------------------------------------------------------------------
    fun cargarCuponesSiEsNecesario(token: String) {
        if (cuponesCargados) return
        cargarCupones(token)
    }

    fun refrescarCupones(token: String) {
        cuponesCargados = false
        cargarCupones(token)
    }

    private fun cargarCupones(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getCuponesDisponibles("Bearer $token")
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    _cupones.value = lista.filter { it.activo }
                    cuponesCargados = true
                } else {
                    _rewardsError.value = "No se pudieron cargar los cupones (código ${response.code()})"
                }
            } catch (e: Exception) {
                _rewardsError.value = "Error de conexión al cargar los cupones."
            }
        }
    }

    /**
     * Dispara las 3 cargas de Rewards en paralelo, respetando caché.
     * Llamar desde RewardsFragment.onViewCreated(), igual que HomeFragment
     * llama a cargarPerfilSiEsNecesario() + cargarReportesSiEsNecesario().
     */
    fun cargarRewardsSiEsNecesario(token: String) {
        cargarPuntosSiEsNecesario(token)
        cargarRankingSiEsNecesario(token)
        cargarCuponesSiEsNecesario(token)
    }

    /** Fuerza recarga de las 3 secciones de Rewards (pull-to-refresh). */
    fun refrescarRewards(token: String) {
        refrescarPuntos(token)
        refrescarRanking(token)
        refrescarCupones(token)
    }

    // ---------------------------------------------------------------------
    // POST /api/canjes/cupon/{id}
    // ---------------------------------------------------------------------
    fun canjearCupon(token: String, cupon: Cupon) {
        viewModelScope.launch {
            _rewardsLoading.value = true
            try {
                val response = RetrofitClient.apiService.canjearCupon("Bearer $token", cupon.id)

                if (response.isSuccessful) {
                    val body = response.body()
                    _canjeResultado.value = CanjeResultado(
                        exito = true,
                        mensaje = body?.mensaje ?: "¡Cupón canjeado con éxito!"
                    )

                    // Si el backend devuelve los puntos restantes, los reflejamos
                    // de inmediato sin esperar una nueva consulta completa.
                    body?.puntosRestantes?.let { restantes ->
                        _historialPuntos.value = _historialPuntos.value?.copy(puntosActuales = restantes)
                    }

                    // Refresca cupones (por si el stock cambió) y el historial completo.
                    refrescarCupones(token)
                    refrescarPuntos(token)
                } else {
                    val mensaje = when (response.code()) {
                        400 -> "No tienes suficientes puntos para este cupón."
                        404 -> "Este cupón ya no está disponible."
                        409 -> "Este cupón ya fue canjeado o se agotó el stock."
                        else -> "No se pudo canjear el cupón (código ${response.code()})."
                    }
                    _canjeResultado.value = CanjeResultado(exito = false, mensaje = mensaje)
                }
            } catch (e: Exception) {
                _canjeResultado.value = CanjeResultado(
                    exito = false,
                    mensaje = "Error de conexión al canjear el cupón. Intenta nuevamente."
                )
            } finally {
                _rewardsLoading.value = false
            }
        }
    }

    /** El Fragment llama esto tras mostrar el Toast/diálogo, para que no se repita. */
    fun consumirCanjeResultado() {
        _canjeResultado.value = null
    }

    /** El Fragment llama esto tras mostrar el error, para que no se repita. */
    fun consumirRewardsError() {
        _rewardsError.value = null
    }
}

/** Resultado de un intento de canje, para que el Fragment decida qué mostrar. */
data class CanjeResultado(
    val exito: Boolean,
    val mensaje: String
)



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



}*/
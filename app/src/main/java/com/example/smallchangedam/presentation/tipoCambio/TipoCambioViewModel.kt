package com.example.smallchangedam.presentation.tipoCambio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallchangedam.data.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la interfaz de usuario
data class TipoCambioUiState(
    val cantidad: String = "1",
    val monedaTengo: String = "EUR",
    val monedaQuiero: String = "USD",
    val tasaActual: Double = 0.0,
    val cargandoTasa: Boolean = false,
    val monedasDisponibles: Map<String, String> = emptyMap(),
    val error: String? = null
)

class TipoCambioViewModel(private val apiService: ApiService) : ViewModel() {

    private val _uiState = MutableStateFlow(TipoCambioUiState())
    val uiState: StateFlow<TipoCambioUiState> = _uiState.asStateFlow()

    init {
        cargarMonedasIniciales()
    }

    private fun cargarMonedasIniciales() {
        viewModelScope.launch {
            try {
                val monedas = apiService.obtenerMonedas()
                _uiState.update { it.copy(monedasDisponibles = monedas) }
                obtenerTasaDesdeApi()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al inicializar monedas") }
            }
        }
    }

    fun obtenerTasaDesdeApi() {
        val tengo = _uiState.value.monedaTengo
        val quiero = _uiState.value.monedaQuiero
        if (tengo.isBlank() || quiero.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(cargandoTasa = true) }
            try {
                val response = apiService.obtenerTipoCambio(monedaOrigen = tengo, monedaDestino = quiero)
                _uiState.update { it.copy(tasaActual = response.tipoCambio, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "No se pudo obtener el tipo de cambio") }
            } finally {
                _uiState.update { it.copy(cargandoTasa = false) }
            }
        }
    }

    fun actualizarCantidad(nuevaCantidad: String) {
        _uiState.update { it.copy(cantidad = nuevaCantidad) }
    }

    fun seleccionarMonedaTengo(moneda: String) {
        _uiState.update { it.copy(monedaTengo = moneda) }
        obtenerTasaDesdeApi()
    }

    fun seleccionarMonedaQuiero(moneda: String) {
        _uiState.update { it.copy(monedaQuiero = moneda) }
        obtenerTasaDesdeApi()
    }

    fun invertirMonedas() {
        _uiState.update {
            it.copy(
                monedaTengo = it.monedaQuiero,
                monedaQuiero = it.monedaTengo
            )
        }
        obtenerTasaDesdeApi()
    }

    fun aplicarConversionPopular(base: String, destino: String) {
        _uiState.update {
            it.copy(
                cantidad = "1",
                monedaTengo = base,
                monedaQuiero = destino
            )
        }
        obtenerTasaDesdeApi()
    }
}
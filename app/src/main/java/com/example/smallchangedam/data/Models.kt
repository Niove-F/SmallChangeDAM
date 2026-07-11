package com.example.smallchangedam.data

import com.google.gson.annotations.SerializedName

// --- AUTENTICACIÓN Y CLIENTES ---

data class RegistroRequest(
    val nombre: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName(value = "ClienteId", alternate = ["id", "clienteId", "userId"]) val clienteId: Int,
    @SerializedName(value = "Nombre", alternate = ["nombre", "userName"]) val nombre: String? = null,
    val mensaje: String? = null
)

data class AuthResponse(
    val mensaje: String
)

data class ValidationErrorResponse(
    val errors: Map<String, List<String>>? = null
)

data class ClienteResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("calificacionVendedor") val calificacionVendedor: Double,
    @SerializedName("cantCalificaciones") val cantCalificaciones: Int,
    @SerializedName("fechaRegistro") val fechaRegistro: String
)

// --- OFERTAS ---

data class OfertaRequest(
    @SerializedName("monedaAEnviar") val monedaAEnviar: String,
    @SerializedName("monedaARecibir") val monedaARecibir: String,
    @SerializedName("cantidad") val cantidad: Double,
    @SerializedName("tipoCambio") val tipoCambio: Double
)

data class OfertaResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("clienteId") val clienteId: Int,
    @SerializedName("nombreUsuario") val nombreUsuario: String?,
    @SerializedName("calificacionUsuario") val calificacionUsuario: Double,
    @SerializedName("monedaAEnviar") val monedaAEnviar: String,
    @SerializedName("monedaARecibir") val monedaARecibir: String,
    @SerializedName("cantidad") val cantidad: Double,
    @SerializedName("tipoCambio") val tipoCambio: Double,
    @SerializedName("estado") val estado: Boolean,
    @SerializedName("fechaCreacion") val fechaCreacion: String
)

data class OfertaUpdateRequest(
    @SerializedName("monedaAEnviar") val monedaAEnviar: String? = null,
    @SerializedName("monedaARecibir") val monedaARecibir: String? = null,
    @SerializedName("cantidad") val cantidad: Double? = null,
    @SerializedName("tipoCambio") val tipoCambio: Double? = null,
    @SerializedName("estado") val estado: Boolean? = null
)

data class OfertaStatusUpdateRequest(
    @SerializedName("estado") val estado: Boolean? = null
)

data class OfertaStatusResponse(
    @SerializedName("mensaje") val mensaje: String? = null
)

// DTO para el buscador de coincidencias inversas
data class BuscarCoincidenciaRequest(
    @SerializedName("monedaAEnviar") val monedaAEnviar: String,
    @SerializedName("monedaARecibir") val monedaARecibir: String,
    @SerializedName("cantidad") val cantidad: Double,
    @SerializedName("tipoCambio") val tipoCambio: Double
)

data class CoincidenciaOfertaResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("cantidad") val cantidad: Double,
    @SerializedName("tipoCambio") val tipoCambio: Double,
    @SerializedName("nombreUsuario") val nombreUsuario: String
)

// --- TRANSACCIONES ---

data class TransaccionRequest(
    @SerializedName("ofertaId") val ofertaId: Int,
    @SerializedName("clienteCompradorId") val clienteCompradorId: Int
)

// --- DIVISAS ---

data class TipoCambioResponse(
    val monedaIn: String,
    val monedaOut: String,
    val tipoCambio: Double,
    val fechaActualizacion: String
)

data class CambioMonedaRequest(
    val monedaIn: String,
    val monedaOut: String,
    val monto: Double
)

data class CambioMonedaResponse(
    val monedaIn: String,
    val monedaOut: String,
    val tipoCambio: Double,
    val monto: Double,
    val montoConvertido: Double,
    val fechaActualizacion: String
)

// El modelo que representa cada par de divisas
data class TipoCambio(
    val origen: String,     // Ej: "USD"
    val destino: String,    // Ej: "PEN"
    val tasa: Double,       // Ej: 3.3968
    val fechaHora: String   // Ej: "11/07 05:15"
)

// Tu clase de estado de la pantalla
data class TipoCambioUiState(
    val cantidad: String = "",
    val tasaActual: Double = 1.0,
    val monedaTengo: String = "USD",
    val monedaQuiero: String = "PEN",
    val cargandoTasa: Boolean = false,
    val monedasDisponibles: List<String> = listOf("USD", "PEN", "EUR"),

    // AQUÍ NACE LA LISTA: Añadimos la lista de conversiones populares con datos de prueba
    val listaTasasDeCambio: List<TipoCambio> = listOf(
        TipoCambio("USD", "PEN", 3.3968, "11/07 05:15"),
        TipoCambio("PEN", "USD", 0.2944, "11/07 05:15"),
        TipoCambio("EUR", "USD", 1.1432, "11/07 05:15"),
        TipoCambio("USD", "EUR", 0.8747, "11/07 05:15")
    )
)

data class TransaccionResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("ofertaId") val ofertaId: Int,
    @SerializedName("clienteCompradorId") val clienteCompradorId: Int,
    // Tu backend maneja "estado" en minúsculas en el TransaccionResponseDTO
    @SerializedName("estado") val estado: String,
    @SerializedName("fechaCreacion") val fechaCreacion: String
)

data class TransaccionUpdateRequest(
    @SerializedName("estado") val estado: String
)
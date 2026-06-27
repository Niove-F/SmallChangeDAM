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

data class AuthResponse(
    val mensaje: String
)

//atrapa errores 400 bad request
data class ValidationErrorResponse(
    val errors: Map<String, List<String>>? = null
)

data class ClienteResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    @SerializedName("promedioCalificacionComprador") val calificacionComprador: Double?,
    @SerializedName("calificacionVendedor") val calificacionVendedor: Double?
)

// --- OFERTAS ---

data class OfertaRequest(
    val clienteId: Int,
    val monedaAEnviar: String,
    val monedaARecibir: String,
    val tipoCambio: Double
)

data class OfertaResponse(
    val id: Int,
    val clienteId: Int,
    val monedaAEnviar: String,
    val monedaARecibir: String,
    val tipoCambio: Double,
    val estado: Boolean
)

// --- TRANSACCIONES ---

data class TransaccionRequest(
    @SerializedName("OfertaId") val ofertaId: Int,
    @SerializedName("ClienteCompradorId") val clienteCompradorId: Int,
    val estado: String
)

data class TransaccionResponse(
    val id: Int,
    @SerializedName("OfertaId") val ofertaId: Int,
    @SerializedName("ClienteCompradorId") val clienteCompradorId: Int,
    val estado: String
)
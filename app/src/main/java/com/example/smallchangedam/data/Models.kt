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
    @SerializedName("ClienteId") val clienteId: Int, // <-- ¡Mapeamos la propiedad de C#!
    @SerializedName("Nombre") val nombre: String? = null,
    val mensaje: String? = null // Por si manejas mensajes de error genéricos
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
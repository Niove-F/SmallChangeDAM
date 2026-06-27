package com.example.smallchangedam.data

import retrofit2.http.*

interface ApiService {

    // --- AUTENTICACIÓN ---
    @POST("api/auth/registro")
    suspend fun registrarUsuario(@Body request: RegistroRequest): ClienteResponse

    @POST("api/auth/login")
    suspend fun loginUsuario(@Body request: LoginRequest): LoginResponse

    // --- CLIENTES ---
    @GET("api/clientes")
    suspend fun listarClientes(): List<ClienteResponse>

    @GET("api/clientes/{id}")
    suspend fun obtenerClientePorId(@Path("id") id: Int): ClienteResponse

    @POST("api/clientes")
    suspend fun crearCliente(@Body request: RegistroRequest): ClienteResponse

    @DELETE("api/clientes/{id}")
    suspend fun eliminarCliente(@Path("id") id: Int): Unit

    // --- OFERTAS ---
    @GET("api/ofertas")
    suspend fun listarOfertas(): List<OfertaResponse>

    @GET("api/ofertas/{id}")
    suspend fun obtenerOfertaPorId(@Path("id") id: Int): OfertaResponse

    @POST("api/ofertas")
    suspend fun crearOferta(@Body request: OfertaRequest): OfertaResponse

    @PUT("api/ofertas/{id}")
    suspend fun actualizarOferta(@Path("id") id: Int, @Body request: OfertaResponse): OfertaResponse

    // --- TRANSACCIONES ---
    @GET("api/transacciones")
    suspend fun listarTransacciones(): List<TransaccionResponse>

    @POST("api/transacciones")
    suspend fun crearTransaccion(@Body request: TransaccionRequest): TransaccionResponse
}
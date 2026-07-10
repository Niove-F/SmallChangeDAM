package com.example.smallchangedam.data

import retrofit2.http.*
import retrofit2.Response
import retrofit2.http.GET
import okhttp3.ResponseBody
interface ApiService {

    @GET("api/divisas/monedas") // Asegúrate de apuntar a la ruta exacta del controlador
    suspend fun obtenerMonedas(): Map<String, String>

    @GET("api/divisas/tipo-cambio")
    suspend fun obtenerTipoCambio(
        @Query("monedaOrigen") monedaOrigen: String,
        @Query("monedaDestino") monedaDestino: String
    ): TipoCambioResponse // Crea o usa tu DTO equivalente a TipoCambioResponseDTO

    @POST("api/divisas/convertir")
    suspend fun convertirMoneda(
        @Body request: CambioMonedaRequest
    ): CambioMonedaResponse // Crea o usa tu DTO equivalente a CambioMonedaResponseDTO

    // --- AUTENTICACIÓN ---
    @POST("api/auth/registro")
    suspend fun registrarUsuario(@Body request: RegistroRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun loginUsuario(@Body request: LoginRequest): LoginResponse


    // --- CLIENTES ---
    @GET("api/clientes")
    suspend fun listarClientes(): List<ClienteResponse>

    @GET("api/clientes/{id}")
    suspend fun obtenerClientePorId(@Path("id") id: Int): ClienteResponse

    // El sistema califica al usuario calculando el nuevo promedio matemático
    @POST("api/clientes/{id}/calificar")
    suspend fun calificarUsuario(
        @Path("id") id: Int,
        @Query("calificacionRecibida") calificacion: Double
    ): Response<Boolean>

    @DELETE("api/clientes/{id}")
    suspend fun eliminarCliente(@Path("id") id: Int): Response<Boolean>


    // --- OFERTAS ---
    @GET("api/ofertas")
    suspend fun listarOfertas(): List<OfertaResponse>

    @GET("api/ofertas/{id}")
    suspend fun obtenerOfertaPorId(@Path("id") id: Int): OfertaResponse

    // Obtiene las ofertas publicadas específicamente por un usuario
    @GET("api/ofertas/usuario/{clienteId}")
    suspend fun listarOfertasPorUsuario(@Path("clienteId") clienteId: Int): List<OfertaResponse>

    // Tu backend pide: AddAsync(CreateOfertaDTO createDto, int clienteId)
    @POST("api/ofertas")
    suspend fun crearOferta(
        @Body request: OfertaRequest,
        @Query("clienteId") clienteId: Int
    ): OfertaResponse

    // Cambiado a OfertaUpdateRequest para admitir nulos parciales como el UpdateOfertaDTO de C#
    @PUT("api/ofertas/{id}")
    suspend fun actualizarOferta(@Path("id") id: Int, @Body request: OfertaUpdateRequest): Response<Boolean>

    // Cambiar estado true/false de la oferta de forma rápida
    @PATCH("api/ofertas/{id}/estado")
    suspend fun actualizarEstadoOferta(@Path("id") id: Int, @Query("nuevoEstado") nuevoEstado: Boolean): Response<Boolean>

    // Invoca tu algoritmo de coincidencia inversa con tolerancia del 2%
    @POST("api/ofertas/buscar-coincidencia")
    suspend fun buscarCoincidenciaInversa(@Body request: BuscarCoincidenciaRequest): CoincidenciaOfertaResponse?


    // --- TRANSACCIONES ---
    // Tu backend exige un userId para filtrar: GetAllAsync(int userId)
    @GET("api/transacciones")
    suspend fun listarTransacciones(@Query("userId") userId: Int): List<TransaccionResponse>

    @GET("api/transacciones/{id}")
    suspend fun obtenerTransaccionPorId(@Path("id") id: Int): TransaccionResponse

    // Tu backend pide: AddAsync(CreateTransaccionDTO createDto, int usuarioId)
    @POST("api/transacciones")
    suspend fun crearTransaccion(
        @Body request: TransaccionRequest,
        @Query("usuarioId") usuarioId: Int
    ): TransaccionResponse

    // Cambia el estado (ej. de "pendiente" a otro) e involucra al servicio de auditoría en C#
    @PUT("api/transacciones/{id}")
    suspend fun actualizarTransaccion(
        @Path("id") id: Int,
        @Body request: TransaccionUpdateRequest,
        @Query("usuarioId") usuarioId: Int
    ): Response<Boolean>

    @DELETE("api/transacciones/{id}")
    suspend fun eliminarTransaccion(@Path("id") id: Int, @Query("usuarioId") usuarioId: Int): Response<Boolean>
}
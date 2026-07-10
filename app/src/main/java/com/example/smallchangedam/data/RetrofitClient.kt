package com.example.smallchangedam.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto sencillo para mantener el token en memoria durante la sesión.
 * (Nota: En una app de producción, es mejor guardar esto en SharedPreferences o DataStore
 * para que el usuario no tenga que hacer login cada vez que cierra la app).
 */
object SessionManager {
    var authToken: String? = null
    var userId: Int? = null // <-- ¡Agrega esto! Así cualquier pantalla puede saber quién está logueado
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000/"

    // 1. Creamos el interceptor que agregará el token a CADA petición
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        // Si tenemos un token guardado en el SessionManager, lo inyectamos
        SessionManager.authToken?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // Continuamos con la petición (ahora con la cabecera incluida)
        chain.proceed(requestBuilder.build())
    }

    // 2. Configuramos el cliente HTTP (OkHttp) para que use nuestro interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    // 3. Pasamos el cliente HTTP modificado a Retrofit
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // <-- ¡Clave! Conectamos OkHttp a Retrofit
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}